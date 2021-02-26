package pa.am.video_catcher.catcher.bilibili.bean;

import pa.am.scipioutils.io.parser.GsonUtil;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.VideoInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * B站发起请求的数据的javaBean
 * @author Alan Min
 * @since 2021/2/23
 */
public class BilibiliApi {

    /**
     * 获取视频播放信息的api地址（前缀）
     */
    public static final String VIDEO_API_URL_PREFIX = "https://api.bilibili.com/x/player/playurl?fnval=80";

    //av号
    private Long avid;

    //分p的id
    private List<Long> cidList;

    //bv号
    private String bvid;

    //视频表面上的url
    private String requestUrl;

    //视频封面url
    private String coverUrl;

    //视频播放相关的json数据（如果多p的话，就是页面打开当前播放的那p的数据）
    private MediaPlay currentMediaPlay;

    //视频信息相关的json数据
    private VideoInfo videoInfo;

    /**
     * 获取最终的视频信息api地址
     */
    public String getApiUrl(String cid) {
        return VIDEO_API_URL_PREFIX + "&cid=" + cid + "&avid=" + avid;
    }

    /**
     * 根据视频播放的json数据，转换为javaBean
     * @param json 视频播放的json数据
     * @return javaBean
     */
    public static MediaPlay getMediaPlay(String json) {
        return GsonUtil.fromJson(json, MediaPlay.class);
    }

    /**
     * 根据视频信息的json数据，转换为javaBean
     * @param json 视频信息的json数据
     * @return javaBean
     */
    public static VideoInfo getVideoInfo(String json) {
        return GsonUtil.fromJson(json, VideoInfo.class);
    }

    /**
     * 从网页script标签里，提取视频信息json
     * @param script 页面html里的script标签里的内容
     * @return 从script标签里提取的视频信息json
     */
    public static String getVideoInfoJsonFromScript(String script) {
        String regex = "(window.__INITIAL_STATE__=)(\\{[\\s\\S]+})(;\\(function[\\s\\S]+\\);)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(script);
        return matcher.find() ? matcher.group(2) : null;
    }

    /**
     * 从网页script标签里，提取视频播放信息json
     * @param script 页面html里的script标签里的内容
     * @return 从script标签里提取的视频播放信息json
     */
    public static String getMediaJsonFromScript(String script) {
        String regex = "(window.__playinfo__=)(\\{[\\s\\S]+})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(script);
        return matcher.find() ? matcher.group(2) : null;
    }

    public void addNewCid(long cid) {
        if(cidList==null) {
            cidList = new ArrayList<>();
        }
        cidList.add(cid);
    }

    //========================================================================

    public MediaPlay getCurrentMediaPlay() {
        return currentMediaPlay;
    }

    public void setCurrentMediaPlay(MediaPlay currentMediaPlay) {
        this.currentMediaPlay = currentMediaPlay;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Long getAvid() {
        return avid;
    }

    public void setAvid(Long avid) {
        this.avid = avid;
    }

    public List<Long> getCidList() {
        return cidList;
    }

    public void setCidList(List<Long> cidList) {
        this.cidList = cidList;
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
