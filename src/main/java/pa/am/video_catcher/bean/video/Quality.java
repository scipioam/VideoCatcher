package pa.am.video_catcher.bean.video;

/**
 * 下载质量
 * @author Alan Min
 * @since 2021/2/19
 */
public enum Quality {

    DEFAULT("默认品质"),
    BEST("最高品质"),
    WORST("最低品质");

    private final String name;

    Quality(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
