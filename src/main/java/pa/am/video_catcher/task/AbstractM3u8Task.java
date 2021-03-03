package pa.am.video_catcher.task;

import javafx.scene.control.ProgressIndicator;
import org.apache.logging.log4j.Logger;
import pa.am.scipioutils.common.StringUtil;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.catcher.m3u8.DownloadListener;
import pa.am.video_catcher.catcher.m3u8.M3u8Catcher;
import pa.am.video_catcher.catcher.m3u8.bean.ErrorVO;

import java.io.File;
import java.util.List;

/**
 * @author Alan Min
 * @since 2021/3/3
 */
public abstract class AbstractM3u8Task extends AbstractTask{

    protected final String url;
    protected final File downloadDir;//下载目录
    protected final String fileName;//下载后的文件名

    protected final M3u8Catcher catcher = new M3u8Catcher();

    public AbstractM3u8Task(Logger log, String url, File downloadDir, String fileName, Integer threadLimit, Integer retryLimit, String fileSuffix) {
        super(log);
        this.url = url;
        this.downloadDir = downloadDir;
        this.fileName = fileName;
        if(threadLimit!=null && threadLimit>0) {
            catcher.setDownloadThreadCount(threadLimit);
        }
        if(retryLimit!=null && retryLimit>0) {
            catcher.setRetryLimit(retryLimit);
        }
        if(StringUtil.isNotNull(fileSuffix)) {
            catcher.setFileSuffix(fileSuffix);
        }
    }

    protected abstract void finishJob(long startTime);

    protected void doDownload(long startTime) {
        updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS,1.0);
        updateMessage("获取元数据中...");
        log.info("m3u8 download task started");
        catcher.setDir(downloadDir.getPath());
        catcher.setFileName(fileName);
        catcher.setUserAgent(GlobalConst.USER_AGENT);
        //设置下载监听器
        catcher.setDownloadListener(new DownloadListener() {
            //进行时
            @Override
            public void onProcessing(int finishedFileCount, int totalFileCount) {
                double percent = ((double) finishedFileCount / (double) totalFileCount);
                updateProgressInfo(percent);
            }
            //单个下载线程完成时
            @Override
            public void onFinishedThread(int totalFileCount, List<ErrorVO> errorVOList) {
                if(errorVOList!=null && errorVOList.size()>0) {
                    for(ErrorVO vo : errorVOList) {
                        log.error("Download error, file index[{}], message[{}], downloadThreadName[{}], Exception:{}",vo.getFileIndex(),vo.getMsg(),vo.getThreadName()
                                , vo.getException()==null ? "null" : vo.getException().toString());
                    }
                }
            }//end onFinishedThread()
            //全部下载线程完成时
            @Override
            public void onTotalThreadFinished() {
                updateProgressInfo(ProgressIndicator.INDETERMINATE_PROGRESS,"正在合并片段...");
            }
        });//end setDownloadListener()
        //开始执行操作，阻塞直到完成
        catcher.doCatch(url);
        updateProgressInfo(1.0);
        //收尾工作
        finishJob(startTime);
        log.info("m3u8 download task finished");
    }

}
