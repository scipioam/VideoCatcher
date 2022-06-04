package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_javafx.ProgressDialog;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.mapper.VideoInfo;
import javafx.application.Platform;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.controller.VideoDownController;

/**
 * 获取视频信息的子线程
 * @author Alan Min
 * @since 2021/2/20
 */
public class GetVideoInfoTask extends AbstractTask{

    private final String url;
    private final VideoDownController controller;
    private final ProgressDialog progressDialog;

    public GetVideoInfoTask(String url, VideoDownController controller, ProgressDialog progressDialog) {
        this.url = url;
        this.controller = controller;
        this.progressDialog = progressDialog;
    }

    @Override
    protected String call() {
        long startTime = System.currentTimeMillis();
        VideoInfo videoInfo = null;
        //获取视频信息
        try {
            videoInfo = YoutubeDL.getVideoInfo(url);
        }catch (Exception e) {
            log.warn("Get video info failed, {}",e.toString());
            e.printStackTrace();
        }
        //结束工作
        finishJob(startTime,videoInfo,url.contains("youtube.com"));
        return null;
    }

    private void finishJob(long startTime, final VideoInfo info, final boolean isYoutubeUrl) {
        keepThreadRunTime(startTime, GlobalConst.THREAD_KEEP_TIME);
        Platform.runLater(()->controller.updateVideoInfo(isYoutubeUrl,info,progressDialog));
    }

}
