package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_javafx.DialogHelper;
import com.github.ScipioAM.scipio_utils_javafx.ProgressDialog;
import javafx.application.Platform;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.bean.video.BiliPage;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.controller.AbstractPageController;

/**
 * @author Alan Min
 * @since 2021/3/2
 */
public class BiliDownloadTask extends AbstractBiliDownTask{

    private final AbstractPageController controller;
    private final ProgressDialog progressDialog;

    public BiliDownloadTask(AbstractPageController controller, Setting setting, BilibiliApi api, BiliPage page, ProgressDialog progressDialog, DownloadMode downloadMode, boolean isNewUrl) {
        super(setting,api,page,downloadMode,isNewUrl);
        this.controller = controller;
        this.progressDialog = progressDialog;
    }

    @Override
    protected String call() {
        long startTime = System.currentTimeMillis();
        updateMessage("开始下载...");
        doDownload(startTime);
        return null;
    }

    protected void finishJob(long startTime, final boolean isSuccess) {
        keepThreadRunTime(startTime, GlobalConst.THREAD_KEEP_TIME);
        controller.unbindTask2Progress(progressDialog);
        Platform.runLater(()->{
            progressDialog.dismiss();
            if(isSuccess) {
                DialogHelper.showAlert(controller.getRootPane(),"下载完成","已成功下载!");
                progressDialog.setProgress(1.0);
                progressDialog.setContent("下载完成");
            }
            else {
                DialogHelper.showAlert(controller.getRootPane(),"下载失败","下载出现错误!");
                progressDialog.setProgress(0.0);
                progressDialog.setContent("下载失败");
            }
        });
    }

}
