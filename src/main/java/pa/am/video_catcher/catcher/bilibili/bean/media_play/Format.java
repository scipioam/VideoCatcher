package pa.am.video_catcher.catcher.bilibili.bean.media_play;

/**
 * B站视频 - 支持的编码格式
 * @author Alan Min
 * @since 2021/2/23
 */
public class Format {

    private Integer quality;
    private String format;
    private String new_description;
    private String display_desc;
    private String superscript;

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

    public String getNew_description() {
        return new_description;
    }

    public void setNew_description(String new_description) {
        this.new_description = new_description;
    }

    public String getDisplay_desc() {
        return display_desc;
    }

    public void setDisplay_desc(String display_desc) {
        this.display_desc = display_desc;
    }

    public String getSuperscript() {
        return superscript;
    }

    public void setSuperscript(String superscript) {
        this.superscript = superscript;
    }
}
