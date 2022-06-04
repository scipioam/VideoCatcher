package pa.am.video_catcher.task;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alan Min
 * @since 2021/2/18
 */
public abstract class AbstractTask extends Task<String> {

    protected final Logger log = LoggerFactory.getLogger(AbstractTask.class);

    /**
     * 保持线程运行时间不低于一段时间，否则硬捱到这个时间
     *
     * @param startTime 线程开始的时间戳
     * @param keepTime  保持的时间长度，单位毫秒
     */
    protected void keepThreadRunTime(long startTime, long keepTime) {
        long timeInterval = System.currentTimeMillis() - startTime;
        if (timeInterval < keepTime) {
            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                log.warn("Thread.sleep has been interrupted when run keepThreadRunTime()");
            }
        }
    }//end keepThreadRunTime()

    /**
     * 更新界面上的进度显示
     *
     * @param percent 进度百分比(范围0-1)
     * @param msg     进度信息
     */
    public void updateProgressInfo(double percent, double max, String msg) {
        updateProgress(percent, max);
        updateMessage(msg);
    }

    public void updateProgressInfo(double percent, String msg) {
        updateProgressInfo(percent, 1.0, msg);
    }

    public void updateProgressInfo(double percent) {
        updateProgressInfo(percent, "下载进度:" + String.format("%.1f", percent * 100.0) + "%");
    }

    public void updateProgressInfoWithErrMsg(double percent, String msg) {
        updateProgressInfo(percent, "下载进度:" + String.format("%.1f", percent * 100.0) + "%" + msg);
    }

    public Logger getLogger() {
        return log;
    }

}
