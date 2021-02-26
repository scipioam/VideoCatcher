package pa.am.video_catcher.catcher.bilibili.bean.video_info;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class LevelInfo {

    private Integer current_level;
    private Integer current_min;
    private Integer current_exp;
    private Integer next_exp;

    public Integer getCurrent_level() {
        return current_level;
    }

    public void setCurrent_level(Integer current_level) {
        this.current_level = current_level;
    }

    public Integer getCurrent_min() {
        return current_min;
    }

    public void setCurrent_min(Integer current_min) {
        this.current_min = current_min;
    }

    public Integer getCurrent_exp() {
        return current_exp;
    }

    public void setCurrent_exp(Integer current_exp) {
        this.current_exp = current_exp;
    }

    public Integer getNext_exp() {
        return next_exp;
    }

    public void setNext_exp(Integer next_exp) {
        this.next_exp = next_exp;
    }
}
