package pa.am.video_catcher.bean.video;

import java.io.File;

/**
 * 设置数据
 * @author Alan Min
 * @since 2021/2/21
 */
public class Setting {

    //下载的url
    private String url;

    //下载目录
    private File downloadDir;

    //下载文件名
    private String fileName;

    //下载方式选择（完整视频还是仅音频等）
    private FormatType formatType;

    //下载品质选择
    private Quality quality;

    //下载格式选择
    private FormatInfo formatInfo;

    //下载后将文件转换为新的格式
    private TransFormat transFormat;

    //重试次数
    private Integer retries;

    //超时时间（单位：秒）
    private Integer timeOut;

    //cookie文件
    private File cookieFile;

    //用户自定义UA
    private String userAgent;

    //下载限速(MB/s)
    private Integer downloadLimit;

    public Setting() { }

    public Setting(String url, File downloadDir, String fileName, FormatType formatType, Quality quality, FormatInfo formatInfo) {
        this.url = url;
        this.downloadDir = downloadDir;
        this.fileName = fileName;
        this.formatType = formatType;
        this.quality = quality;
        this.formatInfo = formatInfo;
    }

    public Setting(String url, File downloadDir, String fileName, FormatType formatType, Quality quality, FormatInfo formatInfo, TransFormat transFormat, Integer retries, Integer timeOut, File cookieFile, String userAgent) {
        this.url = url;
        this.downloadDir = downloadDir;
        this.fileName = fileName;
        this.formatType = formatType;
        this.quality = quality;
        this.formatInfo = formatInfo;
        this.transFormat = transFormat;
        this.retries = retries;
        this.timeOut = timeOut;
        this.cookieFile = cookieFile;
        this.userAgent = userAgent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(File downloadDir) {
        this.downloadDir = downloadDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FormatType getFormatType() {
        return formatType;
    }

    public void setFormatType(FormatType formatType) {
        this.formatType = formatType;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public FormatInfo getFormatInfo() {
        return formatInfo;
    }

    public void setFormatInfo(FormatInfo formatInfo) {
        this.formatInfo = formatInfo;
    }

    public TransFormat getTransFormat() {
        return transFormat;
    }

    public void setTransFormat(TransFormat transFormat) {
        this.transFormat = transFormat;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public File getCookieFile() {
        return cookieFile;
    }

    public void setCookieFile(File cookieFile) {
        this.cookieFile = cookieFile;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public Integer getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(Integer downloadLimit) {
        this.downloadLimit = downloadLimit;
    }
}
