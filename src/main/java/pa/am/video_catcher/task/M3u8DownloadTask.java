package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_javafx.DialogHelper;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import pa.am.video_catcher.controller.M3u8Controller;

import java.io.File;

/**
 * m3u8下载子线程
 * @author Alan Min
 * @since 2021/2/18
 */
public class M3u8DownloadTask extends AbstractM3u8Task {

    private final M3u8Controller controller;

    public M3u8DownloadTask(M3u8Controller controller, String url, File downloadDir, String fileName, Integer threadLimit, Integer retryLimit, String fileSuffix) {
        super(LogManager.getLogger(M3u8DownloadTask.class),url,downloadDir,fileName,threadLimit,retryLimit,fileSuffix);
        this.controller = controller;
    }

    @Override
    protected String call(){
        long startTime = System.currentTimeMillis();
        doDownload(startTime);
        return null;
    }

    @Override
    protected void finishJob(long startTime, boolean isSuccess, String errMsg) {
        keepThreadRunTime(startTime,2000L);
        if(isSuccess) {
            updateProgressInfo(1.0,"下载完成");
            Platform.runLater(()-> {
                controller.unbindTask2Progress();
                DialogHelper.showAlert(controller.getRootPane(),"下载完成","已完成m3u8视频的下载!");
                controller.setBtnDisable(false);
            });
        } else {
            String msg = StringUtil.isNotNull(errMsg) ? ("下载失败，" + errMsg) : "下载失败";
            updateProgressInfo(1.0,msg);
            Platform.runLater(()-> {
                controller.unbindTask2Progress();
                controller.setBtnDisable(false);
            });
        }
    }

}
