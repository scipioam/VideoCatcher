package pa.am.video_catcher.task;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import org.apache.logging.log4j.LogManager;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.jfoenix.DialogHelper;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.catcher.m3u8.DownloadListener;
import pa.am.video_catcher.catcher.m3u8.M3u8Catcher;
import pa.am.video_catcher.catcher.m3u8.bean.ErrorVO;
import pa.am.video_catcher.controller.M3u8Controller;

import java.io.File;
import java.util.List;

/**
 * m3u8下载子线程
 * @author Alan Min
 * @since 2021/2/18
 */
public class M3u8DownloadTask extends AbstractTask {

    private final M3u8Controller controller;
    private final String url;
    private final File downloadDir;//下载目录
    private final String fileName;//下载后的文件名

    private final M3u8Catcher catcher;

    public M3u8DownloadTask(M3u8Controller controller, String url, File downloadDir, String fileName, Integer threadLimit, Integer retryLimit, String fileSuffix) {
        super(LogManager.getLogger(M3u8DownloadTask.class));
        catcher = new M3u8Catcher();
        this.controller = controller;
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

    @Override
    protected String call(){
        long startTime = System.currentTimeMillis();
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
        return null;
    }

    private void finishJob(long startTime) {
        keepThreadRunTime(startTime,2000L);
        updateProgressInfo(1.0,"下载完成");
        controller.unbindTask2Progress();
        Platform.runLater(()-> {
            DialogHelper.showAlert(controller.getRootPane(),"下载完成","已完成m3u8视频的下载!");
            controller.setBtnDisable(false);
        });
    }

}
