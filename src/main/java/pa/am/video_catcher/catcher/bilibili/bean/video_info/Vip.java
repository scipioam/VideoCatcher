package pa.am.video_catcher.catcher.bilibili.bean.video_info;

/**
 * @author Alan Min
 * @since 2021/2/24
 */
public class Vip {

    private Integer vipType;
    private String dueRemark;
    private Integer accessStatus;
    private Integer vipStatus;
    private String vipStatusWarn;
    private Integer theme_type;

    public Integer getVipType() {
        return vipType;
    }

    public void setVipType(Integer vipType) {
        this.vipType = vipType;
    }

    public String getDueRemark() {
        return dueRemark;
    }

    public void setDueRemark(String dueRemark) {
        this.dueRemark = dueRemark;
    }

    public Integer getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(Integer accessStatus) {
        this.accessStatus = accessStatus;
    }

    public Integer getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(Integer vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getVipStatusWarn() {
        return vipStatusWarn;
    }

    public void setVipStatusWarn(String vipStatusWarn) {
        this.vipStatusWarn = vipStatusWarn;
    }

    public Integer getTheme_type() {
        return theme_type;
    }

    public void setTheme_type(Integer theme_type) {
        this.theme_type = theme_type;
    }
}
