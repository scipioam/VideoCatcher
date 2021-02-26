package pa.am.video_catcher.catcher.bilibili.bean.media_play;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * B站视频播放信息 - root bean
 * @author Alan Min
 * @since 2021/2/23
 */
public class MediaPlay {

    private Integer code;

    private String message;

    private Integer ttl;

    private String session;

    private MediaPlayData data;

    private List<Media> getMediaList(boolean isVideo, boolean isSort) {
        if(data==null) {
            return null;
        }
        Dash dash = data.getDash();
        if(dash==null) {
            return null;
        }
        List<Media> mediaList = isVideo ? dash.getVideo() : dash.getAudio();
        if(isSort) {
            Collections.sort(mediaList);
        }
        return mediaList;
    }

    /**
     * 获取音频list（按id从小到大排序）
     * 注意：一旦排过序后，list本身已变化
     */
    public List<Media> getSortedAudioList() {
        return getMediaList(false,true);
    }

    /**
     * 获取视频list（未排序）
     */
    public List<Media> getVideoList() {
        return getMediaList(true,false);
    }

    /**
     * 获取音频list（未排序）
     */
    public List<Media> getAudioList() {
        return getMediaList(false,false);
    }

    public Media getBestQualityVideo() {
        List<Media> videoList = getVideoList();
        return videoList.get(0);
    }

    /**
     * 获取视频播放信息
     * @param index 下标（list已按id从小到大排序过了）
     */
    public Media getVideo(int index) {
        List<Media> videoList = getVideoList();
        if(videoList!=null && videoList.size()>0) {
            return videoList.get(index);
        }
        else {
            return null;
        }
    }

    /**
     * 获取音频播放信息
     * @param index 下标（list已按id从小到大排序过了）
     */
    public Media getAudio(int index) {
        List<Media> audioList = getSortedAudioList();
        if(audioList!=null && audioList.size()>0) {
            return audioList.get(index);
        }
        else {
            return null;
        }
    }

    /**
     * 获取默认质量的视频播放信息
     */
    public Media getDefaultVideo() {
        List<Media> videoList = getVideoList();
        if(videoList!=null && videoList.size()>0) {
            int defaultQualityId = data.getQuality();
            for(Media video : videoList) {
                if(video.getId()==defaultQualityId) {
                    return video;
                }
            }
            return null;
        }
        else {
            return null;
        }
    }

    /**
     * 获取默认的音频播放信息（id最大，也就是质量最好的那个）
     */
    public Media getDefaultAudio() {
        List<Media> audioList = getSortedAudioList();
        if(audioList!=null && audioList.size()>0) {
            return audioList.get(audioList.size()-1);
        }
        else {
            return null;
        }
    }

    public Map<Integer,String> getQualityDescMap() {
        if(data==null) {
            return null;
        }
        List<Integer> qualityList = data.getAccept_quality();
        List<String> descList = data.getAccept_description();
        Map<Integer,String> qualityDescMap = new HashMap<>();
        for(int i=0; i<qualityList.size(); i++) {
            qualityDescMap.put(qualityList.get(i),descList.get(i));
        }
        return qualityDescMap;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public MediaPlayData getData() {
        return data;
    }

    public void setData(MediaPlayData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MediaPlay{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", ttl=" + ttl +
                ", data=" + data +
                '}';
    }
}
