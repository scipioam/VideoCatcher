package pa.am.video_catcher.catcher.bilibili.bean.media_play;

/**
 * @author Alan Min
 * @since 2021/2/23
 */
public class MediaSegment {

    private String Initialization;
    private String indexRange;

    public String getInitialization() {
        return Initialization;
    }

    public void setInitialization(String initialization) {
        Initialization = initialization;
    }

    public String getIndexRange() {
        return indexRange;
    }

    public void setIndexRange(String indexRange) {
        this.indexRange = indexRange;
    }
}
