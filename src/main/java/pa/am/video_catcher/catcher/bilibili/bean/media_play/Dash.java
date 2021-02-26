package pa.am.video_catcher.catcher.bilibili.bean.media_play;

import java.util.List;

/**
 * @author Alan Min
 * @since 2021/2/23
 */
public class Dash {

    private Integer duration;
    private Double minBufferTime;
    private Double min_buffer_time;
    private List<Media> video;
    private List<Media> audio;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getMinBufferTime() {
        return minBufferTime;
    }

    public void setMinBufferTime(Double minBufferTime) {
        this.minBufferTime = minBufferTime;
    }

    public Double getMin_buffer_time() {
        return min_buffer_time;
    }

    public void setMin_buffer_time(Double min_buffer_time) {
        this.min_buffer_time = min_buffer_time;
    }

    public List<Media> getVideo() {
        return video;
    }

    public void setVideo(List<Media> video) {
        this.video = video;
    }

    public List<Media> getAudio() {
        return audio;
    }

    public void setAudio(List<Media> audio) {
        this.audio = audio;
    }
}
