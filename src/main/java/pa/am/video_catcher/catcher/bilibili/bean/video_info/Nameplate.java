package pa.am.video_catcher.catcher.bilibili.bean.video_info;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class Nameplate {

    private Integer pid;
    private String name;
    private String image;
    private Integer expire;
    private String image_enhance;
    private String image_enhance_frame;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getImage_enhance() {
        return image_enhance;
    }

    public void setImage_enhance(String image_enhance) {
        this.image_enhance = image_enhance;
    }

    public String getImage_enhance_frame() {
        return image_enhance_frame;
    }

    public void setImage_enhance_frame(String image_enhance_frame) {
        this.image_enhance_frame = image_enhance_frame;
    }
}
