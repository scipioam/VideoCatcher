package pa.am.video_catcher.catcher.bilibili.bean.video_info;

import java.util.List;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class VideoData {

    private String bvid;
    private Long aid;
    private Integer videos;
    private Integer tid;
    private String tname;
    private Integer copyright;
    private String pic;
    private String title;
    private Long pubdate;
    private Long ctime;
    private String desc;
    private Integer state;
    private Integer duration;
    private Integer mission_id;
    private Right rights;
    private Owner owner;
    private Stat stat;
    private String dynamic;
    private Long cid;
    private Dimension dimension;
    private Boolean no_cache;
    private List<Page> pages;
    private Subtitle subtitle;
    private Label label;
    private UserGarb user_garb;
    private String embedPlayer;

    @Override
    public String toString() {
        return "VideoData{" +
                "bvid='" + bvid + '\'' +
                ", aid=" + aid +
                ", videos=" + videos +
                ", tid=" + tid +
                ", tname='" + tname + '\'' +
                ", copyright=" + copyright +
                ", pic='" + pic + '\'' +
                ", title='" + title + '\'' +
                ", pubdate=" + pubdate +
                ", ctime=" + ctime +
                ", desc='" + desc + '\'' +
                ", state=" + state +
                ", duration=" + duration +
                ", mission_id=" + mission_id +
                ", rights=" + rights +
                ", owner=" + owner +
                ", stat=" + stat +
                ", dynamic='" + dynamic + '\'' +
                ", cid=" + cid +
                ", dimension=" + dimension +
                ", no_cache=" + no_cache +
                ", pages=" + pages +
                ", subtitle=" + subtitle +
                ", label=" + label +
                ", user_garb=" + user_garb +
                ", embedPlayer='" + embedPlayer + '\'' +
                '}';
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public Integer getVideos() {
        return videos;
    }

    public void setVideos(Integer videos) {
        this.videos = videos;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public Integer getCopyright() {
        return copyright;
    }

    public void setCopyright(Integer copyright) {
        this.copyright = copyright;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPubdate() {
        return pubdate;
    }

    public void setPubdate(Long pubdate) {
        this.pubdate = pubdate;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMission_id() {
        return mission_id;
    }

    public void setMission_id(Integer mission_id) {
        this.mission_id = mission_id;
    }

    public Right getRights() {
        return rights;
    }

    public void setRights(Right rights) {
        this.rights = rights;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Boolean getNo_cache() {
        return no_cache;
    }

    public void setNo_cache(Boolean no_cache) {
        this.no_cache = no_cache;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public Subtitle getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Subtitle subtitle) {
        this.subtitle = subtitle;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public UserGarb getUser_garb() {
        return user_garb;
    }

    public void setUser_garb(UserGarb user_garb) {
        this.user_garb = user_garb;
    }

    public String getEmbedPlayer() {
        return embedPlayer;
    }

    public void setEmbedPlayer(String embedPlayer) {
        this.embedPlayer = embedPlayer;
    }
}
