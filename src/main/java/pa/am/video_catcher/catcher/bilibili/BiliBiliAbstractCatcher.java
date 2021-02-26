package pa.am.video_catcher.catcher.bilibili;

import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.io.parser.GsonUtil;
import pa.am.scipioutils.net.http.HttpUtil;
import pa.am.scipioutils.net.http.common.Response;
import pa.am.scipioutils.net.http.common.ResponseDataMode;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.Media;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.Page;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.VideoInfo;
import pa.am.video_catcher.util.WindowsCmdUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * B站视频、封面抓取工具父类
 * @author Alan Min
 * @since 2021/2/23
 */
public abstract class BiliBiliAbstractCatcher {

    protected final Logger log;

    protected final BilibiliApi bilibiliApi = new BilibiliApi();

    protected final HttpUtil httpUtil = new HttpUtil();

    protected DownloadListener downloadListener;

    public BiliBiliAbstractCatcher(Logger log) {
        this.log = log;
    }

    //=======================================================================================

    /**
     * 下载封面图片
     * @param url 视频url
     */
    public abstract void downloadCover(String url) throws BilibiliException;

    /**
     * 下载视频（或单纯视频，或单纯音频）
     * @param url 视频url
     * @param mediaPlay 要下载的分p视频，为null则下载默认质量的第1p视频
     * @param qualityId 视频质量id（来源于json里的），为null则下载json里默认质量的
     * @param downloadMode 下载模式（音视频，还是纯视频，还是纯音频）
     */
    public abstract void downloadMedia(String url, MediaPlay mediaPlay, Integer qualityId, DownloadMode downloadMode) throws BilibiliException;

    //=======================================================================================

    /**
     * 从html页面中获取所需的json数据
     * @param url 用户所看到的视频url
     * @return json数据
     * @throws BilibiliException 请求失败
     * @throws IllegalArgumentException 参数错误
     */
    public BilibiliApi getBilibiliApiFromHtml(String url) throws BilibiliException,IllegalArgumentException {
        check(url);
        log.info("start get BilibiliApi data from Html");
        //发起请求
        Response response = httpUtil.get(url);
        int responseCode = response.getResponseCode();
        //请求成功
        if(responseCode>=200&&responseCode<300) {
            Document document = Jsoup.parse(response.getData());
            Elements scriptNodesArr = document.getElementsByTag("script");
            for(Element scriptNode : scriptNodesArr ) {
                String data = scriptNode.data();
                if(data.contains("window.__playinfo__")) {
                    String mediaJson = BilibiliApi.getMediaJsonFromScript(data);
                    log.info("get mediaPlay json success from html:\n"+mediaJson);
                    MediaPlay mediaPlay = BilibiliApi.getMediaPlay(mediaJson);
                    bilibiliApi.setCurrentMediaPlay(mediaPlay);
                }
                else if(data.contains("window.__INITIAL_STATE__")) {
                    String videoInfoJson = BilibiliApi.getVideoInfoJsonFromScript(data);
                    log.info("get videoInfo json success from html:\n"+videoInfoJson);
                    VideoInfo videoInfo = GsonUtil.fromJson(videoInfoJson,VideoInfo.class);
                    bilibiliApi.setVideoInfo(videoInfo);
                    bilibiliApi.setAvid(videoInfo.getAid());
                    bilibiliApi.setBvid(videoInfo.getBvid());
                    bilibiliApi.setCoverUrl(videoInfo.getCoverUrl());
                    List<Page> pageList = videoInfo.getPages();
                    if(pageList!=null && pageList.size()>0) {
                        List<Long> cidList = new ArrayList<>();
                        for(Page page : pageList) {
                            cidList.add(page.getCid());
                        }
                        bilibiliApi.setCidList(cidList);
                    }
                    break;
                }
            }//end for
            return bilibiliApi;
        }//end outside if
        else {
            throw new BilibiliException("Request html failed, response code:"+responseCode);
        }
    }//end getJsonDataFromHtml()

    /**
     * api请求获取视频播放json
     * @param videoInfo 视频信息数据
     * @param cid 分p的具体id
     * @return json数据
     * @throws BilibiliException 请求失败
     * @throws IllegalArgumentException 参数错误
     */
    public BilibiliApi getJsonFromApi(VideoInfo videoInfo,long cid)throws BilibiliException,IllegalArgumentException {
        String apiUrl = BilibiliApi.VIDEO_API_URL_PREFIX+"&avid="+videoInfo.getAid()+"&cid="+cid;
        check(apiUrl);
        log.info("start get json from api");
        Response response = httpUtil.get(apiUrl);
        int responseCode = response.getResponseCode();
        //请求成功
        if(responseCode>=200&&responseCode<300) {
            String json = response.getData();
            log.info("get mediaPlay json success from api url:\n"+json);
            MediaPlay mediaPlay = GsonUtil.fromJson(json,MediaPlay.class);
            bilibiliApi.setCurrentMediaPlay(mediaPlay);
            bilibiliApi.setAvid(videoInfo.getAid());
            bilibiliApi.setCidList(null);
            bilibiliApi.addNewCid(cid);
            bilibiliApi.setVideoInfo(videoInfo);
            return bilibiliApi;
        }
        else {
            throw new BilibiliException("Request api json data failed, response code:"+responseCode);
        }
    }

    private void check(String url) throws BilibiliException,IllegalArgumentException {
        if(StringUtil.isNull(url)) {
            throw new IllegalArgumentException("url is null");
        }
        else if(!StringUtil.isHttpUrl(url)) {
            throw new IllegalArgumentException("param["+url+"] is not a http url");
        }
        bilibiliApi.setRequestUrl(url);
        //如果没有设置ua，就添加默认ua
        if(StringUtil.isNull(httpUtil.getUserAgent())) {
            httpUtil.setDefaultUserAgent();
        }
    }

    /**
     * 下载
     * @param httpUtil http请求工具
     * @param media 要下载的多媒体信息
     * @param dir 下载文件的目录
     * @param retryLimit 重试上限
     * @return 返回下载好的文件对象，如果下载失败则为null
     */
    protected File download(HttpUtil httpUtil, Media media, String dir, int retryLimit) {
        InputStream in = null;
        FileOutputStream out = null;
        int retryCount = 0;
        File downloadFile = new File(dir + File.separator + System.currentTimeMillis() + "-" + media.getId() + ".mp4");
        String downloadUrl = media.getBaseUrl();
        log.info("start download, url[{}]",downloadUrl);
        while (retryCount<retryLimit) {
            try {
                //发起请求
                Response response = httpUtil.get(downloadUrl, ResponseDataMode.STREAM_ONLY);
                int responseCode = response.getResponseCode();
                if(responseCode<200||responseCode>=300){
                    throw new RuntimeException("Request failed, response code:"+response.getResponseCode());
                }

                //获取要下载的总字节数
                long totalBytes = 0L;
                List<String> list = response.getHeader("Content-Length");
                if(list!=null && list.size()>0) {
                    totalBytes = Long.parseLong(list.get(0));
                }

                in = response.getResponseStream();
                if(in==null) {
                    throw new RuntimeException("inputStream is null");
                }
                out = new FileOutputStream(downloadFile);
                int len;
                byte[] bytes = new byte[2048];
                long downloadedBytes = 0L;

                //开始下载
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                    downloadedBytes += len;
                    if(downloadListener!=null) {
                        downloadListener.onProcess(downloadedBytes,totalBytes);
                    }
                }
                out.flush();

                if(downloadListener!=null) {
                    downloadListener.onFinished(totalBytes,downloadFile,media);
                }
                break;
            }catch (Exception e){
                log.warn("[{}]download media failed, media qualityId[{}], {}",retryCount,media.getId(),e.toString());
                e.printStackTrace();
                retryCount++;
            }finally {
                try {
                    if(in!=null)
                        in.close();
                    if(out!=null)
                        out.close();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }//end finally
        }//end while

        if(retryCount>=retryLimit) {
            log.error("Download media failed at the end, media: {}",media.toString());
            return null;
        }
        else {
            log.info("download success, file total bytes[{}], file path[{}], download url[{}]",downloadFile.length(),downloadFile.getPath(),downloadUrl);
            return downloadFile;
        }
    }//end download()

    /**
     * 音视频合并
     * @param videoFile 纯视频文件
     * @param audioFile 纯音频文件
     * @param dir 输出目录
     * @param fileName 输出文件名
     * @param isDeletePart 是否删除原来的音视频文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void combine(File videoFile, File audioFile, String dir, String fileName, boolean isDeletePart) {
        log.info("start to combine video and audio files");
        String outputTxt = WindowsCmdUtil.combineAV(GlobalConst.FFMPEG_PATH,videoFile.getPath(),audioFile.getPath(),dir+File.separator+fileName);
//        System.out.println(outputTxt);
        if(isDeletePart) {
            videoFile.delete();
            audioFile.delete();
            log.info("original files deleted");
        }
        log.info("combine finished");
    }

    //=======================================================================================

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public BilibiliApi getRequestApi() {
        return bilibiliApi;
    }
}
