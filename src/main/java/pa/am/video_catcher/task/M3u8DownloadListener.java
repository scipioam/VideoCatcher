package pa.am.video_catcher.task;

import javafx.scene.control.ProgressIndicator;
import pa.am.video_catcher.catcher.m3u8.DownloadListener;
import pa.am.video_catcher.catcher.m3u8.M3u8Catcher;
import pa.am.video_catcher.catcher.m3u8.bean.ErrorVO;

import java.util.List;

/**
 * @author Alan Scipio
 * @since 2022/6/4
 */
public class M3u8DownloadListener implements DownloadListener {

    private final AbstractM3u8Task task;

    private final M3u8Catcher catcher;

    public M3u8DownloadListener(AbstractM3u8Task task, M3u8Catcher catcher) {
        this.task = task;
        this.catcher = catcher;
    }

    /**
     * 进行时
     */
    @Override
    public void onProcessing(int finishedFileCount, int totalFileCount) {
        double percent = ((double) finishedFileCount / (double) totalFileCount);
        String errMsg = catcher.haveFailedThreads() ? ", 失败的下载子线程[" + catcher.getFailThreadCount().intValue() + "/" + catcher.getDownloadThreadCount() + "]" : "";
        task.updateProgressInfoWithErrMsg(percent, errMsg);
    }

    /**
     * 单个下载线程完成时
     */
    @Override
    public void onFinishedThread(int totalFileCount, List<ErrorVO> errorVOList) {
        if (errorVOList != null && errorVOList.size() > 0) {
            //日志打印
            for (ErrorVO vo : errorVOList) {
                task.getLogger().error("Download error, file index[{}], message[{}], downloadThreadName[{}], Exception:{}", vo.getFileIndex(), vo.getMsg(), vo.getThreadName()
                        , vo.getException() == null ? "null" : vo.getException().toString());
            }
        }
    }

    /**
     * 全部下载线程完成时
     */
    @Override
    public void onTotalThreadFinished() {
        if (!catcher.haveFailedThreads()) {
            task.updateProgressInfo(ProgressIndicator.INDETERMINATE_PROGRESS, "正在合并片段...");
        }
    }

}
