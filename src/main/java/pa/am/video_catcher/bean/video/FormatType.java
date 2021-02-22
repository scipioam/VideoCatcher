package pa.am.video_catcher.bean.video;

/**
 * 选择下载音频还是视频
 * @author Alan Min
 * @since 2021/2/19
 */
public enum FormatType {

    FULL("完整视频(视频+音频)"),
    VIDEO_ONLY("仅视频"),
    AUDIO_ONLY("仅音频");

    private final String name;

    FormatType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
