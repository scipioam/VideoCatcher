package pa.am.video_catcher.bean.video;

import com.sapher.youtubedl.YoutubeDLRequest;

/**
 * 下载格式的具体信息，根据DownloadFormat而定
 * @author Alan Min
 * @since 2021/2/19
 */
public enum FormatInfo {

    ORIGINAL("默认格式"),

    //视频
    MP4("mp4"),
    FLV("flv"),
    THIRD_GP("3gp"),
    WEBM("webm"),

    //音频
    MP3("mp3"),
    FLAC("flac"),
    AAC("aac"),
    M4A("m4a"),
    WAV("wav")
    ;

    private final String suffix;

    FormatInfo(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public static void setAudioFormat(FormatInfo info, YoutubeDLRequest request) {
        switch (info)
        {
            case MP3:
                request.setOption("audio-format","mp3");
                break;
            case FLAC:
                request.setOption("audio-format","flac");
                break;
            case AAC:
                request.setOption("audio-format","aac");
                break;
            case M4A:
                request.setOption("audio-format","m4a");
                break;
            case WAV:
                request.setOption("audio-format","wav");
                break;
        }
    }



}
