import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_io.parser.GsonUtil;
import com.github.ScipioAM.scipio_utils_net.http.HttpUtil;
import com.github.ScipioAM.scipio_utils_net.http.bean.ResponseResult;
import com.github.ScipioAM.scipio_utils_net.http.common.ResponseDataMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import pa.am.video_catcher.catcher.bilibili.BilibiliCatcher;
import pa.am.video_catcher.catcher.bilibili.BilibiliUtil;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.Media;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.VideoInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author Alan Min
 * @since 2021/2/22
 */
public class BiliTest {

    private final String VIDEO_URL = "https://www.bilibili.com/video/BV1P54y1W7Pc";

    @Test
    public void testGetVideoInfoFromApi()
    {
        String apiUrl = "https://api.bilibili.com/x/player/playurl?fnval=80&cid=295538295&avid=544081908";

        HttpUtil httpUtil = new HttpUtil();
        httpUtil.setDefaultUserAgent();
        ResponseResult response = httpUtil.get(apiUrl);
        if(response.getResponseCode()>0) {
            String json = response.getData();
            System.out.println("json:\n"+json);
            MediaPlay video = GsonUtil.fromJson(json, MediaPlay.class);
            System.out.println("\n\n"+video.toString());
        }
        else {
            System.out.println("request failed, response code:"+response.getResponseCode());
        }
    }

    @Test
    public void testBvAvConvert()
    {
        String bv = "BV1vb411c7eh";
        String av = "35556243";
        BilibiliUtil util = new BilibiliUtil();

        System.out.println("BV号["+bv+"]转AV号的结果: "+util.bvToAv(bv));
        System.out.println("AV号["+av+"]转BV号的结果: "+util.avToBv(av));
    }

    @Test
    public void testGetJson()
    {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.setDefaultUserAgent();

        ResponseResult response = httpUtil.get(VIDEO_URL);
        if(response.getResponseCode()>0) {
            Document document = Jsoup.parse(response.getData());
            Elements scriptNodesArr = document.getElementsByTag("script");
            for(Element scriptNode : scriptNodesArr ) {
                String data = scriptNode.data();
                if(data.contains("window.__playinfo__")) {
                    String mediaJson = BilibiliApi.getMediaJsonFromScript(data);
                    System.out.println("mediaJson:\n"+mediaJson);
                    MediaPlay mediaPlay = GsonUtil.fromJson(mediaJson,MediaPlay.class);
                    System.out.println(mediaPlay);
                }
                else if(data.contains("window.__INITIAL_STATE__")) {
                    String videoInfoJson = BilibiliApi.getVideoInfoJsonFromScript(data);
                    System.out.println("VideoInfoJson:\n"+videoInfoJson);
                    VideoInfo videoInfo = GsonUtil.fromJson(videoInfoJson,VideoInfo.class);
                    System.out.println(videoInfo);
                    break;
                }
            }
            System.out.println("\n\nend");
        }
        else {
            System.out.println("Request failed, response code:"+response.getResponseCode());
        }
    }

    @Test
    public void testDownload()
    {
        MediaPlay mediaPlay = null;
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.setDefaultUserAgent();

        System.out.println("开始获取视频信息");
        ResponseResult response = httpUtil.get(VIDEO_URL);
        if(response.getResponseCode()>0) {
            Document document = Jsoup.parse(response.getData());
            Elements scriptNodesArr = document.getElementsByTag("script");
            for(Element scriptNode : scriptNodesArr ) {
                String data = scriptNode.data();
                if(data.contains("window.__playinfo__")) {
                    String mediaJson = BilibiliApi.getMediaJsonFromScript(data);
                    System.out.println("mediaJson:\n"+mediaJson);
                    mediaPlay = GsonUtil.fromJson(mediaJson,MediaPlay.class);
                    System.out.println(mediaPlay);
                    System.out.println("获取视频信息成功");
                    break;
                }
            }
        }
        else {
            System.out.println("Request failed, response code:"+response.getResponseCode());
            return;
        }

        if(mediaPlay!=null) {
            //下载视频
            List<Media> videoList = mediaPlay.getVideoList();
            Media video = videoList.get(0);
            download(httpUtil,video);
            //下载音频
//            List<Media> audioList = mediaPlay.getSortedAudioList();
//            Media audio = audioList.get(audioList.size()-1);
//            download(httpUtil,audio);
        }
        else {
            System.out.println("mediaPlay is null");
        }
    }

    /**
     * 下载
     */
    private void download(HttpUtil httpUtil, Media media) {
        httpUtil.addRequestHeader("referer","https://www.bilibili.com/");//必加，否则返回403错误
//        httpUtil.setReqHeaderParam("range","byte=0-9999");//部分下载

        InputStream in = null;
        FileOutputStream out = null;
        try {
            System.out.println("开始请求下载");
            ResponseResult response = httpUtil.get(media.getBaseUrl(), ResponseDataMode.STREAM_ONLY);
            int responseCode = response.getResponseCode();
            if(responseCode<200||responseCode>=300){
                throw new RuntimeException("Request failed, response code:"+response.getResponseCode());
            }
            System.out.println("请求成功，开始下载...");

            //下载总字节数
            long totalBytes;
            String contentLenStr = response.getHeader("Content-Length");
            if(StringUtil.isNotNull(contentLenStr)) {
                totalBytes = Long.parseLong(contentLenStr);
                System.out.println("总字节数："+totalBytes);
            }
            else {
                System.out.println("获取总字节数失败");
            }

            in = response.getResponseStream();
            if(in==null) {
                throw new RuntimeException("inputStream is null");
            }
            File downloadFile = new File("E:\\ATest\\qualityId-"+media.getId()+".mp4");
            out = new FileOutputStream(downloadFile);
            int len;
            byte[] bytes = new byte[2048];
//            long downloadedBytes = 0L;

            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);

                //显示进度
//                downloadedBytes += len;
//                System.out.println("下载进度(字节数):"+downloadedBytes+"/"+totalBytes);
            }
            out.flush();
            System.out.println("下载完成，总字节数:"+downloadFile.length());
        }catch (Exception e){
            System.out.println("下载失败, "+e);
            e.printStackTrace();
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
    }

    @Test
    public void testCatcher()
    {
        BilibiliCatcher catcher = new BilibiliCatcher();
        catcher.setDir("E:\\ATest");
        catcher.setFileName("test00.mp4");

        System.out.println("开始执行");
        //下载封面图
//        BilibiliApi api = catcher.getBilibiliApiFromHtml(VIDEO_URL);
//        catcher.downloadCover(api.getCoverUrl());

        //下载视频（默认质量），并合并音视频
        catcher.downloadMedia(VIDEO_URL,null,null, DownloadMode.FULL);

        //直接运行ffmpeg
//        String o = WindowsCmdUtil.combineAV(GlobalConst.FFMPEG_PATH,"E:\\ATest\\qualityId-16.mp4","E:\\ATest\\qualityId-30280.mp4","E:\\ATest\\t0.mp4");
//        System.out.println(o);

        System.out.println("完成");
    }

}
