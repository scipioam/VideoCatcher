package pa.am.video_catcher.catcher.bilibili;

import org.apache.logging.log4j.LogManager;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.io.FileUtil;
import pa.am.scipioutils.net.http.common.Response;
import pa.am.scipioutils.net.http.common.ResponseDataMode;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.Media;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;

import java.io.File;
import java.io.InputStream;

/**
 * @author Alan Min
 * @since 2021/2/25
 */
public class BilibiliCatcher extends BiliBiliAbstractCatcher{

    //重试上限
    private int retryLimit = 10;

    //合并后的文件存储目录
    private String dir = null;

    //合并后的视频文件名称
    private String fileName = null;

    //发起请求时的ua，为null则按java默认的来
    private String userAgent = null;

    private boolean isDeletePart = true;//下载并合并后，是否删除片段文件

    public BilibiliCatcher() {
        super(LogManager.getLogger(BilibiliCatcher.class));
    }

    //=======================================================================================

    /**
     * 下载封面图片
     * @param coverUrl 视频封面的url
     */
    @Override
    public void downloadCover(String coverUrl) throws BilibiliException{
        if(StringUtil.isNull(coverUrl)) {
            throw new IllegalArgumentException("coverUrl is null");
        }
        //检查ua
        httpUtil.setUserAgent(userAgent);
        if(StringUtil.isNull(httpUtil.getUserAgent())) {
            httpUtil.setDefaultUserAgent();
        }

        String coverFileName;
        if(StringUtil.isNull(fileName)) {
            String[] arr = coverUrl.split("/");
            coverFileName = arr[arr.length-1];
        }
        else {
            coverFileName = fileName;
        }
        int retryCount = 0;//重试次数
        log.info("start request html page for download cover");
        while (retryCount<retryLimit) {
            //发起图片请求
            Response response = httpUtil.get(coverUrl, ResponseDataMode.STREAM_ONLY);
            //开始下载图片
            InputStream in = response.getResponseStream();
            try {
                FileUtil.out2LocalFile(dir + File.separator + coverFileName,in);
                break;
            }catch (Exception e) {
                log.warn("[{}]download cover failed, {}",retryCount,e.toString());
                retryCount++;
            }
        }
        if(retryCount>=retryLimit) {
            String errMsg = "Download cover failed at the end";
            log.error(errMsg);
            throw new BilibiliException(errMsg);
        }
        else {
            log.info("download cover success, url:{}",coverUrl);
        }
    }

    /**
     * 下载视频（或单纯视频，或单纯音频）
     * @param url 视频url
     * @param mediaPlay 要下载的分p视频，为null则下载默认质量的第1p视频
     * @param qualityId 视频质量id（来源于json里的），为null则下载json里默认质量的
     * @param downloadMode 下载模式（音视频，还是纯视频，还是纯音频）
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void downloadMedia(String url, MediaPlay mediaPlay, Integer qualityId, DownloadMode downloadMode) throws BilibiliException {
        //检查json数据
        if(mediaPlay==null) {
            getBilibiliApiFromHtml(url);
            mediaPlay = bilibiliApi.getCurrentMediaPlay();
        }
        //检查ua
        httpUtil.setUserAgent(userAgent);
        if(StringUtil.isNull(httpUtil.getUserAgent())) {
            httpUtil.setDefaultUserAgent();
        }
        //检查目录，如果没有就创建
        File dirFile = new File(dir);
        if(!dirFile.exists()) {
            dirFile.mkdirs();
        }
        //下载
        httpUtil.setReqHeaderParam("referer","https://www.bilibili.com/");//必加，否则返回403错误
        if(downloadMode==DownloadMode.VIDEO_ONLY) {
            log.info("Download mode is VIDEO_ONLY");
            Media video = (qualityId==null ? mediaPlay.getDefaultVideo() : mediaPlay.getBestQualityVideo());
            File videoFile = download(httpUtil,video,dir,retryLimit,true,true);
            if(videoFile==null) {
                throw new BilibiliException("Download video failed");
            }
        }
        else if(downloadMode==DownloadMode.AUDIO_ONLY) {
            log.info("Download mode is AUDIO_ONLY");
            Media audio = mediaPlay.getDefaultAudio();
            File audioFile = download(httpUtil,audio,dir,retryLimit,false,true);
            if(audioFile==null) {
                throw new BilibiliException("Download audio failed");
            }
        }
        else {
            log.info("Download mode is FULL");
            Media video = (qualityId==null ? mediaPlay.getDefaultVideo() : mediaPlay.getBestQualityVideo());
            File videoFile = download(httpUtil,video,dir,retryLimit,true,false);
            if(videoFile==null) {
                throw new BilibiliException("Download video failed");
            }
            Media audio = mediaPlay.getDefaultAudio();
            File audioFile = download(httpUtil,audio,dir,retryLimit,false,true);
            if(audioFile==null) {
                throw new BilibiliException("Download audio failed");
            }
            //音视频合并
            if(downloadListener!=null) {
                downloadListener.onStartCombine();
            }
            //检查文件名
            if(StringUtil.isNull(fileName)) {
                String[] arr = video.getMimeType().split("/");
                fileName = System.currentTimeMillis() + "." + arr[arr.length-1];
            }
            combine(videoFile,audioFile,dir,fileName,isDeletePart);
        }
    }

    //=======================================================================================

    /**
     * 设置登录coolie
     */
    public BilibiliCatcher setLoginCookie(String sessdata, String bili_jct) {
        if(StringUtil.isNull(sessdata) || StringUtil.isNull(bili_jct)) {
            return this;
        }
        httpUtil.setReqHeaderParam("Cookie","SESSDATA="+sessdata+"; bili_jct="+bili_jct);
        return this;
    }

    public int getRetryLimit() {
        return retryLimit;
    }

    public BilibiliCatcher setRetryLimit(int retryLimit) {
        this.retryLimit = retryLimit;
        return this;
    }

    public String getDir() {
        return dir;
    }

    public BilibiliCatcher setDir(String dir) {
        this.dir = dir;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public BilibiliCatcher setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public BilibiliCatcher setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public boolean isDeletePart() {
        return isDeletePart;
    }

    public BilibiliCatcher setDeletePart(boolean deletePart) {
        isDeletePart = deletePart;
        return this;
    }
}
