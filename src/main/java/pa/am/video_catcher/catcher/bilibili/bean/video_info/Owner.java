package pa.am.video_catcher.catcher.bilibili.bean.video_info;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class Owner {

    private Long mid;
    private String name;
    private String face;

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }
}
