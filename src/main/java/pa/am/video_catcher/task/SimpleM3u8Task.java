package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_javafx.DialogHelper;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.scene.control.Label;
import pa.am.video_catcher.controller.SimpleModeController;

import java.io.File;

/**
 * @author Alan Min
 * @since 2021/3/3
 */
public class SimpleM3u8Task extends AbstractM3u8Task {

    private final SimpleModeController controller;
    private final JFXProgressBar progressBar;
    private final Label label_progress;

    public SimpleM3u8Task(String url, File downloadDir, String fileName, SimpleModeController controller, JFXProgressBar progressBar, Label label_progress) {
        super(url, downloadDir, fileName, null, null, null);
        this.controller = controller;
        this.progressBar = progressBar;
        this.label_progress = label_progress;
    }

    @Override
    protected String call() {
        long startTime = System.currentTimeMillis();
        doDownload(startTime);
        return null;
    }

    @Override
    protected void finishJob(long startTime, boolean isSuccess, String errMsg) {
        keepThreadRunTime(startTime, 2000L);
        updateProgressInfo(1.0, "下载完成");

        if (isSuccess) {
            updateProgressInfo(1.0, "下载完成");
            Platform.runLater(() -> {
                controller.unbindTask2Progress(label_progress, progressBar);
                DialogHelper.showAlert(controller.getRootPane(), "下载完成", "已完成m3u8视频的下载!");
            });
        } else {
            String msg = StringUtil.isNotNull(errMsg) ? ("下载失败，" + errMsg) : "下载失败";
            updateProgressInfo(1.0, msg);
            Platform.runLater(() -> controller.unbindTask2Progress(label_progress, progressBar));
        }
    }
}
