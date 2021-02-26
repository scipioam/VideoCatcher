package pa.am.video_catcher.bean.video;

import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.VideoInfo;

/**
 * B站视频分p信息
 * @author Alan Min
 * @since 2021/2/26
 */
public class BiliPage {

    private String title;

    private Long cid = 0L;

    private Long aid = 0L;

    private MediaPlay mediaPlay;

    private VideoInfo videoInfo;

    public BiliPage() { }

    public BiliPage(String title) {
        this.title = title;
    }

    public BiliPage(String title, Long cid, Long aid) {
        this.title = title;
        this.cid = cid;
        this.aid = aid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public MediaPlay getMediaPlay() {
        return mediaPlay;
    }

    public void setMediaPlay(MediaPlay mediaPlay) {
        this.mediaPlay = mediaPlay;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }
}
