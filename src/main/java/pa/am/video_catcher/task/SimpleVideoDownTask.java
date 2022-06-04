package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_javafx.DialogHelper;
import com.jfoenix.controls.JFXProgressBar;
import com.sapher.youtubedl.YoutubeDLResponse;
import javafx.application.Platform;
import javafx.scene.control.Label;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.controller.AbstractPageController;

/**
 * 视频下载线程（基于youtube-dl这一著名python工具）（极简模式下）
 * @author Alan Min
 * @since 2021/2/22
 */
public class SimpleVideoDownTask extends AbstractYDLTask {

    private final JFXProgressBar progressBar;
    private final Label label_progress;

    public SimpleVideoDownTask(AbstractPageController controller, Setting setting, JFXProgressBar progressBar, Label label_progress) {
        super(controller, setting);
        this.progressBar = progressBar;
        this.label_progress = label_progress;
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
        updateProgressInfo(1.0,"下载完成,文件在桌面");
        try {
            Thread.sleep(1000L);
        }catch (InterruptedException e) {
            log.warn("Thread.sleep has been interrupted");
        }
        Platform.runLater(()->{
            controller.unbindTask2Progress(label_progress,progressBar);
            DialogHelper.showAlert(controller.getRootPane(),"下载完成","已成功下载视频!\n文件在桌面");
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
            Platform.runLater(()-> {
                controller.unbindTask2Progress(label_progress, progressBar);
                DialogHelper.showAlert(controller.getRootPane(), "下载失败", "下载格式不支持!");
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
                controller.unbindTask2Progress(label_progress,progressBar);
                DialogHelper.showAlert(controller.getRootPane(),"下载失败","下载时发生错误!");
            });
        }
    }//end onFinishedWithError()

}
