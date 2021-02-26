package pa.am.video_catcher.catcher.bilibili.bean.video_info;

import java.util.List;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class Subtitle {

    private Boolean allow_submit;
    private List<String> list;

    public Boolean getAllow_submit() {
        return allow_submit;
    }

    public void setAllow_submit(Boolean allow_submit) {
        this.allow_submit = allow_submit;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
