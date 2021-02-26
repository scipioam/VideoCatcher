package pa.am.video_catcher.catcher.bilibili.bean.media_play;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alan Min
 * @since 2021/2/23
 */
public class Media implements Comparable<Media>{

    private Integer id;
    private String baseUrl;
//    private String base_url;
    private List<String> backupUrl;
//    private List<String> backup_url;
    private Long bandwidth;
    private String mimeType;
//    private String mime_type;
    private String codecs;
    private Integer width;
    private Integer height;
    private String frameRate;
//    private String frame_rate;
    private String sar;
    private Integer startWithSap;
//    private Integer start_with_sap;
    private MediaSegment SegmentBase;
//    private MediaSegment segment_base;
    private Integer codecid;

    /**
     * 当前多媒体文件是否为纯视频文件
     * @return 返回true代表是纯视频文件，返回false代表是纯音频文件
     */
    public boolean isVideo() {
        return mimeType.contains("video");
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", baseUrl='" + baseUrl + '\'' +
                ", backupUrl=" + backupUrl +
                ", bandwidth=" + bandwidth +
                ", mimeType='" + mimeType + '\'' +
                ", codecs='" + codecs + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", frameRate='" + frameRate + '\'' +
                ", sar='" + sar + '\'' +
                ", startWithSap=" + startWithSap +
                ", SegmentBase=" + SegmentBase +
                ", codecid=" + codecid +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<String> getBackupUrl() {
        return backupUrl;
    }

    public void setBackupUrl(List<String> backupUrl) {
        this.backupUrl = backupUrl;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCodecs() {
        return codecs;
    }

    public void setCodecs(String codecs) {
        this.codecs = codecs;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
    }

    public String getSar() {
        return sar;
    }

    public void setSar(String sar) {
        this.sar = sar;
    }

    public Integer getStartWithSap() {
        return startWithSap;
    }

    public void setStartWithSap(Integer startWithSap) {
        this.startWithSap = startWithSap;
    }

    public MediaSegment getSegmentBase() {
        return SegmentBase;
    }

    public void setSegmentBase(MediaSegment segmentBase) {
        SegmentBase = segmentBase;
    }

    public Integer getCodecid() {
        return codecid;
    }

    public void setCodecid(Integer codecid) {
        this.codecid = codecid;
    }

    @Override
    public int compareTo(@NotNull Media o) {
        return (this.id - o.id);
    }

}
