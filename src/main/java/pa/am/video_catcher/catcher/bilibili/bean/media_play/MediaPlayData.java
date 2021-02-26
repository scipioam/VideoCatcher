package pa.am.video_catcher.catcher.bilibili.bean.media_play;

import java.util.List;

/**
 * B站视频 - 视频播放详细信息
 * @author Alan Min
 * @since 2021/2/23
 */
public class MediaPlayData {

    private String from;
    private String result;
    private String message;
    private Integer quality;
    private String format;
    private Long timelength;
    private String accept_format;
    private List<String> accept_description;
    private List<Integer> accept_quality;
    private Integer video_codecid;
    private String seek_param;
    private String seek_type;
    private Dash dash;
    private List<Format> support_formats;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Long getTimelength() {
        return timelength;
    }

    public void setTimelength(Long timelength) {
        this.timelength = timelength;
    }

    public String getAccept_format() {
        return accept_format;
    }

    public void setAccept_format(String accept_format) {
        this.accept_format = accept_format;
    }

    public List<String> getAccept_description() {
        return accept_description;
    }

    public void setAccept_description(List<String> accept_description) {
        this.accept_description = accept_description;
    }

    public List<Integer> getAccept_quality() {
        return accept_quality;
    }

    public void setAccept_quality(List<Integer> accept_quality) {
        this.accept_quality = accept_quality;
    }

    public Integer getVideo_codecid() {
        return video_codecid;
    }

    public void setVideo_codecid(Integer video_codecid) {
        this.video_codecid = video_codecid;
    }

    public String getSeek_param() {
        return seek_param;
    }

    public void setSeek_param(String seek_param) {
        this.seek_param = seek_param;
    }

    public String getSeek_type() {
        return seek_type;
    }

    public void setSeek_type(String seek_type) {
        this.seek_type = seek_type;
    }

    public Dash getDash() {
        return dash;
    }

    public void setDash(Dash dash) {
        this.dash = dash;
    }

    public List<Format> getSupport_formats() {
        return support_formats;
    }

    public void setSupport_formats(List<Format> support_formats) {
        this.support_formats = support_formats;
    }

    @Override
    public String toString() {
        return "MediaPlayData{" +
                "from='" + from + '\'' +
                ", result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", quality=" + quality +
                ", format='" + format + '\'' +
                ", timelength=" + timelength +
                ", accept_format='" + accept_format + '\'' +
                ", accept_description=" + accept_description +
                ", accept_quality=" + accept_quality +
                ", video_codecid=" + video_codecid +
                ", seek_param='" + seek_param + '\'' +
                ", seek_type='" + seek_type + '\'' +
                ", dash=" + dash +
                ", support_formats=" + support_formats +
                '}';
    }
}
