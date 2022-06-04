package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import javafx.scene.control.ProgressIndicator;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.catcher.m3u8.M3u8Catcher;
import pa.am.video_catcher.catcher.m3u8.bean.ErrorVO;

import java.io.File;

/**
 * @author Alan Min
 * @since 2021/3/3
 */
public abstract class AbstractM3u8Task extends AbstractTask {

    protected final String url;
    protected final File downloadDir;//下载目录
    protected final String fileName;//下载后的文件名

    protected final M3u8Catcher catcher;
    protected final M3u8DownloadListener downloadListener;

    public AbstractM3u8Task(String url, File downloadDir, String fileName, Integer threadLimit, Integer retryLimit, String fileSuffix) {
        this.url = url;
        this.downloadDir = downloadDir;
        this.fileName = fileName;
        catcher = new M3u8Catcher();
        downloadListener = new M3u8DownloadListener(this, catcher);
        if (threadLimit != null && threadLimit > 0) {
            catcher.setDownloadThreadCount(threadLimit);
        }
        if (retryLimit != null && retryLimit > 0) {
            catcher.setRetryLimit(retryLimit);
        }
        if (StringUtil.isNotNull(fileSuffix)) {
            catcher.setFileSuffix(fileSuffix);
        }
    }

    protected abstract void finishJob(long startTime, boolean isSuccess, String errMsg);

    protected void doDownload(long startTime) {
        boolean isSuccess = true;
        String errMsg = null;
        try {
            updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1.0);
            updateMessage("获取元数据中...");
            log.info("m3u8 download task started");
            catcher.setDir(downloadDir.getPath());
            catcher.setFileName(fileName);
            catcher.setUserAgent(GlobalConst.USER_AGENT);
            //设置下载监听器
            catcher.setDownloadListener(downloadListener);//end setDownloadListener()
            //开始执行操作，阻塞直到完成
            catcher.doCatch(url);
            updateProgressInfo(1.0);
        } catch (Exception e) {
            isSuccess = false;
            if (StringUtil.isNotNull(e.getMessage())) {
                errMsg = e.getMessage();
            }
            log.error("m3u8 download failed: {}", e.toString());
            e.printStackTrace();
        }
        if (catcher.haveFailedThreads()) {
            isSuccess = false;
            ErrorVO errorVO = catcher.getErrorVOList().get(0);
            errMsg = errorVO.getMsg() + ". " + errorVO.getException().toString();
        }
        //收尾工作
        finishJob(startTime, isSuccess, errMsg);
        log.info("m3u8 download task finished");
    }

}
