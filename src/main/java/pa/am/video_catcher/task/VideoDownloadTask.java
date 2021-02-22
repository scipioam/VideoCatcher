package pa.am.video_catcher.task;

import com.sapher.youtubedl.*;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import pa.am.scipioutils.jfoenix.DialogHelper;
import pa.am.scipioutils.jfoenix.ProgressDialog;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.controller.AbstractPageController;

/**
 * 视频下载线程（基于youtube-dl这一著名python工具）
 * @author Alan Min
 * @since 2021/2/19
 */
public class VideoDownloadTask extends AbstractDownloadTask {

    private final ProgressDialog progressDialog;

    public VideoDownloadTask(AbstractPageController controller, Setting setting, ProgressDialog progressDialog) {
        super(LogManager.getLogger(VideoDownloadTask.class),controller,setting);
        this.progressDialog = progressDialog;
    }

    @Override
    protected String call() {
        long startTime = System.currentTimeMillis();
        updateMessage("开始下载...");
        doDownload(setting,startTime);
        return null;
    }

    /**
     * 成功下载完后的结束工作
     */
    @Override
    protected void onFinishedWithSuccess(long startTime, YoutubeDLResponse response) {
        //重命名文件，修改后缀
        renameDownloadFile(response,setting);
        keepThreadRunTime(startTime,2000L);
        updateProgressInfo(1.0,"下载完成");
        try {
            Thread.sleep(1000L);
        }catch (InterruptedException e) {
            log.warn("Thread.sleep has been interrupted");
        }
        Platform.runLater(()->{
            controller.unbindTask2Progress(progressDialog);
            progressDialog.dismiss();
            DialogHelper.showAlert(controller.getRootPane(),"下载完成","已成功下载视频!");
        });
    }

    /**
     * 下载失败后的结束工作
     * @param isFormatNotAvailable 失败是否因为请求的格式不支持
     */
    @Override
    protected void onFinishedWithError(long startTime, boolean isFormatNotAvailable) {
        keepThreadRunTime(startTime,2000L);
        if(isFormatNotAvailable) {
            updateProgressInfo(0.95,"下载失败");
            try {
                Thread.sleep(1000L);
            }catch (InterruptedException e) {
                log.warn("Thread.sleep has been interrupted");
            }
            Platform.runLater(()->{
                controller.unbindTask2Progress(progressDialog);
                progressDialog.dismiss();
                DialogHelper.showAlert(controller.getRootPane(),"下载失败","下载格式不支持!");
            });
        }
        else {
            updateProgressInfo(0.95,"下载出错");
            try {
                Thread.sleep(1000L);
            }catch (InterruptedException e) {
                log.warn("Thread.sleep has been interrupted");
            }
            Platform.runLater(()->{
                controller.unbindTask2Progress(progressDialog);
                progressDialog.dismiss();
                DialogHelper.showAlert(controller.getRootPane(),"下载失败","下载时发生错误!");
            });
        }
    }

}
