package pa.am.video_catcher.bean;

import java.lang.reflect.Field;

/**
 * @author Alan Scipio
 * @since 2022/6/1
 */
public class WebUrl {

    /*
        例：https://madou.club/category/s#?page=2
        rootPath: https://madou.club
        subPath: /category/
        lastPath: s#
        params: page=2
     */

    private String rootPath;

    private String subPath;

    private String lastPath;

    private String params;

    public static WebUrl create() {
        return new WebUrl();
    }

    public void printFields() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            System.out.println("==================== WebUrl fields start ====================");
            for (Field field : fields) {
                System.out.println("["+field.getName()+"]"+field.get(this));
            }
            System.out.println("==================== WebUrl fields end ====================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrlExcludeLastPath() {
        return rootPath + subPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public WebUrl setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public String getSubPath() {
        return subPath;
    }

    public WebUrl setSubPath(String subPath) {
        this.subPath = subPath;
        return this;
    }

    public String getLastPath() {
        return lastPath;
    }

    public WebUrl setLastPath(String lastPath) {
        this.lastPath = lastPath;
        return this;
    }

    public String getParams() {
        return params;
    }

    public WebUrl setParams(String params) {
        this.params = params;
        return this;
    }
}
