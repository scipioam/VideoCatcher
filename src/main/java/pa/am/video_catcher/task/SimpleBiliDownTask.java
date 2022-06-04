package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_javafx.DialogHelper;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.scene.control.Label;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.bean.video.BiliPage;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.controller.AbstractPageController;

/**
 * @author Alan Min
 * @since 2021/3/3
 */
public class SimpleBiliDownTask extends AbstractBiliDownTask{

    private final AbstractPageController controller;
    private final JFXProgressBar progressBar;
    private final Label label_progress;

    public SimpleBiliDownTask(Setting setting, BilibiliApi api, BiliPage page, DownloadMode downloadMode, boolean isNewUrl, AbstractPageController controller, JFXProgressBar progressBar, Label label_progress) {
        super(setting, api, page, downloadMode, isNewUrl);
        this.controller = controller;
        this.progressBar = progressBar;
        this.label_progress = label_progress;
    }

    @Override
    protected String call() {
        long startTime = System.currentTimeMillis();
        updateMessage("开始下载...");
        doDownload(startTime);
        return null;
    }

    @Override
    protected void finishJob(long startTime, boolean isSuccess) {
        keepThreadRunTime(startTime, GlobalConst.THREAD_KEEP_TIME);
        controller.unbindTask2Progress(label_progress,progressBar);
        Platform.runLater(()->{
            if(isSuccess) {
                DialogHelper.showAlert(controller.getRootPane(),"下载完成","已成功下载!");
                progressBar.setProgress(1.0);
                label_progress.setText("下载完成,文件在桌面");
            }
            else {
                DialogHelper.showAlert(controller.getRootPane(),"下载失败","下载出现错误!");
                progressBar.setProgress(0.0);
                label_progress.setText("下载失败");
            }
        });
    }

}
