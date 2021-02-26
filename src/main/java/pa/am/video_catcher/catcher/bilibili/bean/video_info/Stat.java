package pa.am.video_catcher.catcher.bilibili.bean.video_info;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class Stat {

    private Long aid;
    private Long view;
    private Integer danmaku;
    private Integer reply;
    private Integer favorite;
    private Long coin;
    private Integer share;
    private Integer now_rank;
    private Integer his_rank;
    private Long like;
    private Integer dislike;
    private String evaluation;
    private String argue_msg;
    private Long viewseo;

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public Long getView() {
        return view;
    }

    public void setView(Long view) {
        this.view = view;
    }

    public Integer getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(Integer danmaku) {
        this.danmaku = danmaku;
    }

    public Integer getReply() {
        return reply;
    }

    public void setReply(Integer reply) {
        this.reply = reply;
    }

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    public Long getCoin() {
        return coin;
    }

    public void setCoin(Long coin) {
        this.coin = coin;
    }

    public Integer getShare() {
        return share;
    }

    public void setShare(Integer share) {
        this.share = share;
    }

    public Integer getNow_rank() {
        return now_rank;
    }

    public void setNow_rank(Integer now_rank) {
        this.now_rank = now_rank;
    }

    public Integer getHis_rank() {
        return his_rank;
    }

    public void setHis_rank(Integer his_rank) {
        this.his_rank = his_rank;
    }

    public Long getLike() {
        return like;
    }

    public void setLike(Long like) {
        this.like = like;
    }

    public Integer getDislike() {
        return dislike;
    }

    public void setDislike(Integer dislike) {
        this.dislike = dislike;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getArgue_msg() {
        return argue_msg;
    }

    public void setArgue_msg(String argue_msg) {
        this.argue_msg = argue_msg;
    }

    public Long getViewseo() {
        return viewseo;
    }

    public void setViewseo(Long viewseo) {
        this.viewseo = viewseo;
    }
}
