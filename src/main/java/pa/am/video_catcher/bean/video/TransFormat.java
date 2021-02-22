package pa.am.video_catcher.bean.video;

/**
 * @author Alan Min
 * @since 2021/2/21
 */
public enum TransFormat {

    NO_TRANS("不转换", "no"),

    //视频
    MP4("原文件转为mp4", "mp4"),
    FLV("原文件转为flv", "flv"),
    MKV("原文件转为mkv", "mkv"),
    AVI("原文件转为avi", "avi"),
    WEBM("原文件转为webm", "webm"),

    //音频
    OGG("原文件转为ogg", "ogg")
    ;

    private final String info;
    private final String suffix;

    TransFormat(String info, String suffix) {
        this.info = info;
        this.suffix = suffix;
    }

    public String getInfo() {
        return info;
    }

    public String getSuffix() {
        return suffix;
    }
}
