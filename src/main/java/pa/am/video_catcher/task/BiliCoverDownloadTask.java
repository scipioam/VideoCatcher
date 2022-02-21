package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_javafx.DialogHelper;
import com.github.ScipioAM.scipio_utils_javafx.ProgressDialog;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.catcher.bilibili.BilibiliCatcher;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.controller.BiliDownController;

/**
 * @author Alan Min
 * @since 2021/3/2
 */
public class BiliCoverDownloadTask extends AbstractTask{

    protected final BiliDownController controller;
    protected final Setting setting;
    protected final String coverUrl;
    protected final ProgressDialog progressDialog;

    public BiliCoverDownloadTask(BiliDownController controller, Setting setting, String coverUrl, ProgressDialog progressDialog) {
        super(LogManager.getLogger(BiliCoverDownloadTask.class));
        this.controller = controller;
        this.setting = setting;
        this.coverUrl = coverUrl;
        this.progressDialog = progressDialog;
    }

    @Override
    protected String call(){
        log.info("Start run BiliCoverDownloadTask");
        long startTime = System.currentTimeMillis();
        boolean isSuccess = false;
        updateMessage("下载中...");

        BilibiliCatcher catcher = new BilibiliCatcher();
        BilibiliApi newApi;
        String finalCoverUrl = coverUrl;
        if(finalCoverUrl==null) {
            try {
                log.info("New video url, get cover url from html");
                newApi = catcher.getBilibiliApiFromHtml(setting.getUrl());
                finalCoverUrl = newApi.getCoverUrl();
                controller.getLastApi().setFromOtherApi(newApi);
            }catch (Exception e) {
                log.error("Get bilibili data from html failed, url[{}], {}",setting.getUrl(),e.toString());
                e.printStackTrace();
                finishJob(startTime,false);
                return null;
            }
        }
        else {
            log.info("Old video url, use last coverUrl[{}]",finalCoverUrl);
        }

        catcher.setFileName(setting.getFileName());
        catcher.setDir(setting.getDownloadDir().getPath());
        catcher.setUserAgent(setting.getUserAgent());
        if(setting.getRetries()!=null) {
            catcher.setRetryLimit(setting.getRetries());
        }
        try {
            log.info("Start download bilibili cover, url[{}]",finalCoverUrl);
            catcher.downloadCover(finalCoverUrl);
            isSuccess = true;
            log.info("Download bilibili cover succeed");
        }catch (Exception e) {
            log.error("Download bilibili cover failed, downloadUrl[{}], {}",finalCoverUrl,e.toString());
            e.printStackTrace();
        }
        finishJob(startTime,isSuccess);
        return null;
    }

    private void finishJob(long startTime,final boolean isSuccess) {
        keepThreadRunTime(startTime, GlobalConst.THREAD_KEEP_TIME);
        controller.unbindTask2Progress(progressDialog);
        Platform.runLater(()->{
            progressDialog.dismiss();
            if(isSuccess) {
                DialogHelper.showAlert(controller.getRootPane(),"下载完成","已成功下载封面!");
            }
            else {
                DialogHelper.showAlert(controller.getRootPane(),"下载失败","下载B站封面失败!");
            }
        });
    }

}
