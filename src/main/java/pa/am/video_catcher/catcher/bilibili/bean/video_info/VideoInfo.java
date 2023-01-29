package pa.am.video_catcher.catcher.bilibili.bean.video_info;

import java.util.List;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class VideoInfo {

    private Long aid;
    private String bvid;
    private Integer p;
    private String episode;
    private VideoData videoData;
    private UpData upData;
//    private List<String> staffData;
    private List<Tag> tags;
//    private List<Related> related;
//    private Spec spec;
    private Boolean isClient;
//    private Error error;
    private String player;
//    private Playurl playurl;
//    private User user;
//    private CidMap cidMap;
//    private String isRecAutoPlay;
//    private String autoPlayNextVideo;
//    private Boolean elecState;
//    private ElecFullInfo elecFullInfo;
//    private AdData adData;
//    private BofqiParams bofqiParams;
//    private List<String> insertScripts;

    /**
     * 获取分p信息
     */
    public List<Page> getPages() {
        if(videoData==null) {
            return null;
        }
        return videoData.getPages();
    }

    public Page getPageByCid(long cid) {
        if(videoData==null) {
            return null;
        }
        List<Page> pageList = videoData.getPages();
        for(Page page : pageList) {
            if(page.getCid()==cid) {
                return page;
            }
        }
        return null;
    }

    /**
     * 获取封面图片的url
     */
    public String getCoverUrl() {
        if(videoData==null) {
            return null;
        }
        return videoData.getPic();
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "aid=" + aid +
                ", bvid='" + bvid + '\'' +
                ", p=" + p +
                ", episode='" + episode + '\'' +
                ", videoData=" + videoData +
                ", upData=" + upData +
                ", tags=" + tags +
                ", isClient=" + isClient +
                ", player='" + player + '\'' +
//                ", isRecAutoPlay='" + isRecAutoPlay + '\'' +
//                ", autoPlayNextVideo='" + autoPlayNextVideo + '\'' +
//                ", elecState=" + elecState +
//                ", insertScripts=" + insertScripts +
                '}';
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public Integer getP() {
        return p;
    }

    public void setP(Integer p) {
        this.p = p;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public VideoData getVideoData() {
        return videoData;
    }

    public void setVideoData(VideoData videoData) {
        this.videoData = videoData;
    }

    public UpData getUpData() {
        return upData;
    }

    public void setUpData(UpData upData) {
        this.upData = upData;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Boolean getClient() {
        return isClient;
    }

    public void setClient(Boolean client) {
        isClient = client;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

}
