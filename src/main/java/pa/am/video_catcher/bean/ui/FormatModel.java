package pa.am.video_catcher.bean.ui;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sapher.youtubedl.mapper.VideoFormat;
import javafx.beans.property.*;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.Media;

/**
 * @author Alan Min
 * @since 2021/2/20
 */
public class FormatModel extends RecursiveTreeObject<FormatModel> {

    //格式id
    private StringProperty formatId;

    //关于分辨率的简要信息（b站时为音视频类型）
    private StringProperty note;

    //分辨率
    private StringProperty resolution;

    //文件类型（后缀）
    private StringProperty extension;

    //文件大小
    private LongProperty fileSize;

    //格式信息
    private StringProperty formatInfo;

    //视频帧率（仅音频时为0）(youtube-dl专用)
    private IntegerProperty fps;

    //作为b站视频信息时，与之对应的av号
    private LongProperty aid;

    //作为b站视频信息时，与之对应的分p信息
    private LongProperty cid;

    //编码格式（b站专用）
    private StringProperty codec;

    public static FormatModel build(boolean isYoutubeUrl, VideoFormat originalData) {
        FormatModel model = new FormatModel();
        model.setFormatId(originalData.formatId);
        model.setFormatInfo(originalData.format);
        model.setExtension(originalData.ext);
        model.setNote(originalData.formatNote);
        model.setFps(originalData.fps);
        model.setFileSize(originalData.filesize);
        if(originalData.width==0 && originalData.height==0) {
            model.setResolution(isYoutubeUrl ? "纯音频" : "unknown");
        }
        else {
            model.setResolution(originalData.width+"x"+originalData.height);
        }
        return model;
    }

    public static FormatModel build(Media media, String resolutionInfo) {
        FormatModel model = new FormatModel();
        model.setFormatId(media.getId().toString());
        model.setCodec(media.getCodecs());
        model.setResolution(resolutionInfo);
        model.setNote( media.getMimeType().contains("video") ? "视频" : "音频" );
        return model;
    }

    /**
     * 获取文件大小的显示文本（易于人阅读的）
     */
    public String getFileSizeInfo() {
        long fileSize = getFileSize();
        double fileSize_d;
        StringBuilder sb = new StringBuilder();
        if(fileSize<1048576L) {
            fileSize_d = fileSize / 1024.0;
            sb.append(String.format("%.1f",fileSize_d)).append("KB");
        }
        else if(fileSize<1073741824L) {
            fileSize_d = fileSize / 1048576.0;
            sb.append(String.format("%.1f",fileSize_d)).append("MB");
        }
        else {
            fileSize_d = fileSize / 1073741824.0;
            sb.append(String.format("%.1f",fileSize_d)).append("GB");
        }
        return sb.toString();
    }

    public String getFormatId() {
        return strGet(formatId);
    }

    public StringProperty formatIdProperty() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = strSet(this.formatId,formatId);
    }

    public String getNote() {
        return strGet(note);
    }

    public StringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note = strSet(this.note,note);
    }

    public String getResolution() {
        return strGet(resolution);
    }

    public StringProperty resolutionProperty() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = strSet(this.resolution,resolution);
    }

    public String getExtension() {
        return strGet(extension);
    }

    public StringProperty extensionProperty() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = strSet(this.extension,extension);
    }

    public long getFileSize() {
        return longGet(fileSize);
    }

    public LongProperty fileSizeProperty() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = longSet(this.fileSize,fileSize);
    }

    public String getFormatInfo() {
        return strGet(formatInfo);
    }

    public StringProperty formatInfoProperty() {
        return formatInfo;
    }

    public void setFormatInfo(String formatInfo) {
        this.formatInfo = strSet(this.formatInfo,formatInfo);
    }

    public int getFps() {
        return intGet(fps);
    }

    public IntegerProperty fpsProperty() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = intSet(this.fps,fps);
    }

    public long getAid() {
        return longGet(aid);
    }

    public LongProperty aidProperty() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = longSet(this.aid,aid);
    }

    public long getCid() {
        return longGet(cid);
    }

    public LongProperty cidProperty() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = longSet(this.cid,cid);
    }

    public String getCodec() {
        return strGet(codec);
    }

    public StringProperty codecProperty() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = strSet(this.codec,codec);
    }

    //================================================

    private int intGet(IntegerProperty prop) {
        return (prop==null ? 0 : prop.get());
    }

    private long longGet(LongProperty prop) {
        return (prop==null ? 0L : prop.get());
    }

    private String strGet(StringProperty prop) {
        return (prop==null ? null : prop.get());
    }

    private IntegerProperty intSet(IntegerProperty prop, Integer data) {
        if(data==null) {
            prop = null;
        }
        else {
            if(prop==null) {
                prop = new SimpleIntegerProperty();
            }
            prop.set(data);
        }
        return prop;
    }

    private LongProperty longSet(LongProperty prop, Long data) {
        if(data==null) {
            prop = null;
        }
        else {
            if(prop==null) {
                prop = new SimpleLongProperty();
            }
            prop.set(data);
        }
        return prop;
    }

    private StringProperty strSet(StringProperty prop, String data) {
        if(data==null) {
            prop = null;
        }
        else {
            if(prop==null) {
                prop = new SimpleStringProperty();
            }
            prop.set(data);
        }
        return prop;
    }

}
