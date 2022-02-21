package pa.am.video_catcher.ui;

import com.sapher.youtubedl.DownloadProgressCallback;
import pa.am.video_catcher.task.AbstractYDLTask;

/**
 * youtube-dl的下载进程监听器
 * @author Alan Min
 * @since 2021/2/22
 */
public class YDLDownloadListener implements DownloadProgressCallback {

    private final AbstractYDLTask task;
    private final boolean isYoutube;//是否为下载youtube的视频，为true代表是
    private final boolean isFormat;//是否设置了format参数，为true代表设置了

    private double lastProgress = 0.0;

    public YDLDownloadListener(AbstractYDLTask task, boolean isYoutube, boolean isFormat) {
        this.task = task;
        this.isYoutube = isYoutube;
        this.isFormat = isFormat;
    }

    @Override
    public void onProgressUpdate(float progress, long etaInSeconds) {
        //youtube下载时的进度显示
        if(isYoutube && !isFormat) {
            if(progress>lastProgress) {
                task.updateProgressInfo(progress,100.0,"(1/2)下载视频\n剩余"+getTimeStr(etaInSeconds));
                lastProgress = progress;
            }
            else {
                task.updateProgressInfo(progress,100.0,"(2/2)下载音频\n剩余"+getTimeStr(etaInSeconds));
            }
        }
        //其他视频下载时的进度显示
        else {
            task.updateProgressInfo(progress,100.0,"下载中...\n剩余"+getTimeStr(etaInSeconds));
        }
    }//end onProgressUpdate()

    private String getTimeStr(long etaInSeconds) {
        StringBuilder time = new StringBuilder();
        if(etaInSeconds < 60L) {
            time.append(etaInSeconds).append("秒");
        }
        else if(etaInSeconds < 3600L) {
            long min = etaInSeconds / 60L;
            long second = etaInSeconds % 60L;
            time.append(min).append("分").append(second).append("秒");
        }
        else {
            long hour = etaInSeconds / 3600L;
            long min = (etaInSeconds%3600L) / 60L;
            long second = (etaInSeconds%3600L) % 60L;
            time.append(hour).append("时").append(min).append("分").append(second).append("秒");
        }
        return time.toString();
    }

}
