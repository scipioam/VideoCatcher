package pa.am.video_catcher.catcher.bilibili.bean.video_info;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class UpData {

    private String mid;
    private String name;
    private Boolean approve;
    private String sex;
    private String rank;
    private String face;
    private String DisplayRank;
    private Integer regtime;
    private Integer spacesta;
    private String birthday;
    private String place;
    private String description;
    private Integer article;
//    private List<String> attentions;
    private Long fans;
    private Integer friend;
    private Integer attention;
    private String sign;
    private LevelInfo level_info;
//    private Pendant pendant;
    private Nameplate nameplate;
    private Official Official;
    private OfficialVerify official_verify;
    private Vip vip;
    private Integer archiveCount;

    @Override
    public String toString() {
        return "UpData{" +
                "mid='" + mid + '\'' +
                ", name='" + name + '\'' +
                ", approve=" + approve +
                ", sex='" + sex + '\'' +
                ", rank='" + rank + '\'' +
                ", face='" + face + '\'' +
                ", DisplayRank='" + DisplayRank + '\'' +
                ", regtime=" + regtime +
                ", spacesta=" + spacesta +
                ", birthday='" + birthday + '\'' +
                ", place='" + place + '\'' +
                ", description='" + description + '\'' +
                ", article=" + article +
                ", fans=" + fans +
                ", friend=" + friend +
                ", attention=" + attention +
                ", sign='" + sign + '\'' +
                ", level_info=" + level_info +
                ", nameplate=" + nameplate +
                ", Official=" + Official +
                ", official_verify=" + official_verify +
                ", vip=" + vip +
                ", archiveCount=" + archiveCount +
                '}';
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getDisplayRank() {
        return DisplayRank;
    }

    public void setDisplayRank(String displayRank) {
        DisplayRank = displayRank;
    }

    public Integer getRegtime() {
        return regtime;
    }

    public void setRegtime(Integer regtime) {
        this.regtime = regtime;
    }

    public Integer getSpacesta() {
        return spacesta;
    }

    public void setSpacesta(Integer spacesta) {
        this.spacesta = spacesta;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getArticle() {
        return article;
    }

    public void setArticle(Integer article) {
        this.article = article;
    }

    public Long getFans() {
        return fans;
    }

    public void setFans(Long fans) {
        this.fans = fans;
    }

    public Integer getFriend() {
        return friend;
    }

    public void setFriend(Integer friend) {
        this.friend = friend;
    }

    public Integer getAttention() {
        return attention;
    }

    public void setAttention(Integer attention) {
        this.attention = attention;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public LevelInfo getLevel_info() {
        return level_info;
    }

    public void setLevel_info(LevelInfo level_info) {
        this.level_info = level_info;
    }

    public Nameplate getNameplate() {
        return nameplate;
    }

    public void setNameplate(Nameplate nameplate) {
        this.nameplate = nameplate;
    }

    public pa.am.video_catcher.catcher.bilibili.bean.video_info.Official getOfficial() {
        return Official;
    }

    public void setOfficial(pa.am.video_catcher.catcher.bilibili.bean.video_info.Official official) {
        Official = official;
    }

    public OfficialVerify getOfficial_verify() {
        return official_verify;
    }

    public void setOfficial_verify(OfficialVerify official_verify) {
        this.official_verify = official_verify;
    }

    public Vip getVip() {
        return vip;
    }

    public void setVip(Vip vip) {
        this.vip = vip;
    }

    public Integer getArchiveCount() {
        return archiveCount;
    }

    public void setArchiveCount(Integer archiveCount) {
        this.archiveCount = archiveCount;
    }
}
