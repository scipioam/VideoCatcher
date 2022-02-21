package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_javafx.ProgressDialog;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.bean.ui.FormatVO;
import pa.am.video_catcher.catcher.bilibili.BilibiliCatcher;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.Media;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.Page;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.VideoInfo;
import pa.am.video_catcher.controller.BiliDownController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取b站视频信息的线程
 * @author Alan Min
 * @since 2021/2/26
 */
public class GetBiliVideoInfoTask extends AbstractTask{

    private final String url;
    private final BiliDownController controller;
    private final ProgressDialog progressDialog;
    private final Map<Long, List<FormatVO>> formatListMap;
    private final boolean isFirst;
    private final VideoInfo videoInfo;
    private final Long cid;
    private final String sessdata;
    private final String bili_jct;
    private final String userAgent;

    public GetBiliVideoInfoTask(String url, BiliDownController controller, ProgressDialog progressDialog, Map<Long, List<FormatVO>> formatListMap, boolean isFirst, VideoInfo videoInfo, Long cid, String sessdata, String bili_jct, String userAgent) {
        super(LogManager.getLogger(GetBiliVideoInfoTask.class));
        this.url = url;
        this.controller = controller;
        this.progressDialog = progressDialog;
        this.formatListMap = formatListMap;
        this.isFirst = isFirst;
        this.videoInfo = videoInfo;
        this.cid = cid;
        this.sessdata = sessdata;
        this.bili_jct = bili_jct;
        this.userAgent = userAgent;
    }

    @Override
    protected String call() {
        long startTime = System.currentTimeMillis();
        BilibiliCatcher catcher = new BilibiliCatcher();
        if(StringUtil.isNotNull(sessdata) && StringUtil.isNotNull(bili_jct)) {
            catcher.setLoginCookie(sessdata,bili_jct);
        }
        if(StringUtil.isNotNull(userAgent)) {
            catcher.setUserAgent(userAgent);
        }
        BilibiliApi api = null;
        try {
            if(isFirst) {
                api = catcher.getBilibiliApiFromHtml(url);
            }
            else {
                api = catcher.getJsonFromApi(videoInfo,cid);
            }
        }catch (Exception e){
            log.warn("Get bilibili video info failed, {}",e.toString());
            e.printStackTrace();
        }
        //将当前播放信息加入map
        setMap(api);
        //收尾工作
        finishJob(startTime,api);
        return null;
    }

    private void setMap(BilibiliApi api) {
        if(api==null) {
            return;
        }
        MediaPlay mediaPlay = api.getCurrentMediaPlay();
        if(mediaPlay==null) {
            return;
        }
        VideoInfo videoInfo = api.getVideoInfo();
        if(videoInfo==null) {
            return;
        }
        Page page = (isFirst ? videoInfo.getPages().get(0) : videoInfo.getPageByCid(cid));
        //视频信息
        List<Media> videoList = mediaPlay.getVideoList();
        Map<Integer,String> qualityDescMap = mediaPlay.getQualityDescMap();
        List<FormatVO> modelList = new ArrayList<>();
        for(int i=0; i<videoList.size(); i+=2) {
            Media video = videoList.get(i);
            FormatVO model = FormatVO.build(video,qualityDescMap.get(video.getId()));
            model.setCid(page.getCid());
            model.setAid(videoInfo.getAid());
            modelList.add(model);
        }
        //音频信息
        List<Media> audioList = mediaPlay.getSortedAudioList();
        for(Media audio : audioList) {
            FormatVO model = FormatVO.build(audio,null);
            model.setCid(page.getCid());
            model.setAid(videoInfo.getAid());
            modelList.add(model);
        }
        formatListMap.put(page.getCid(),modelList);
    }

    private void finishJob(long startTime, final BilibiliApi api) {
        keepThreadRunTime(startTime, GlobalConst.THREAD_KEEP_TIME);
        Platform.runLater(()-> controller.updateVideoInfo(api,progressDialog,isFirst,cid));
    }

}
