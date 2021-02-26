package pa.am.video_catcher.catcher.m3u8.bean;

/**
 * @author Alan Min
 * @since 2021/2/9
 */
public class ErrorVO {

    //文件在全局数组中的下标
    private Integer fileIndex;

    //出错信息
    private String msg;

    //出错所在的线程名称
    private String threadName;

    //异常对象（可能为null）
    private Exception exception;

    public ErrorVO() { }

    public ErrorVO(Integer fileIndex, String msg, String threadName) {
        this.fileIndex = fileIndex;
        this.msg = msg;
        this.threadName = threadName;
    }

    public ErrorVO(Integer fileIndex, String msg, String threadName, Exception exception) {
        this.fileIndex = fileIndex;
        this.msg = msg;
        this.threadName = threadName;
        this.exception = exception;
    }

    public Integer getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(Integer fileIndex) {
        this.fileIndex = fileIndex;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
