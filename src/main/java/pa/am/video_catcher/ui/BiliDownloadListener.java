package pa.am.video_catcher.ui;

import javafx.scene.control.ProgressIndicator;
import pa.am.video_catcher.catcher.bilibili.DownloadListener;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.Media;
import pa.am.video_catcher.task.AbstractBiliDownTask;

import java.io.File;

/**
 * @author Alan Min
 * @since 2021/3/2
 */
public class BiliDownloadListener implements DownloadListener {

    private final AbstractBiliDownTask task;

    public BiliDownloadListener(AbstractBiliDownTask task) {
        this.task = task;
    }

    /**
     * 下载进行时
     * @param isVideo 是否在下载视频（为false则是在下载音频）
     * @param downloadedBytes 已下载的字节数
     * @param totalBytes 总字节数
     */
    @Override
    public void onDownload(boolean isVideo, long downloadedBytes, long totalBytes) {
        double percent = (double) downloadedBytes / (double) totalBytes;
        String msg = (isVideo ? "下载视频中" : "下载音频中");
        task.updateProgressInfo(percent, msg);
    }

    /**
     * 开始合并音视频
     */
    @Override
    public void onStartCombine() {
        task.updateProgressInfo(ProgressIndicator.INDETERMINATE_PROGRESS,"合并音视频中");
    }

    /**
     * 下载完成时
     * @param totalBytes 总字节数
     * @param downloadedFile 下载的文件
     * @param media 下载的多媒体信息
     * @param isLastDownload 是否为最后一次下载
     */
    @Override
    public void onDownloadFinished(long totalBytes, File downloadedFile, Media media, boolean isLastDownload) {

    }

}
