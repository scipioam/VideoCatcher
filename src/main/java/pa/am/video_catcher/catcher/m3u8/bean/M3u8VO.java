package pa.am.video_catcher.catcher.m3u8.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * m3u8下载时的信息bean
 *
 * @author Alan Min
 * @since 2021/2/8
 */
public class M3u8VO {

    //ts片段是否被加密
    private Boolean isEncrypted = false;

    //加解密算法名称
    private String method;

    //所有ts片段下载链接
    private List<TsFragment> tsFragments;

    public Boolean getEncrypted() {
        return isEncrypted;
    }

    public M3u8VO setEncrypted(Boolean encrypted) {
        isEncrypted = encrypted;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public M3u8VO setMethod(String method) {
        this.method = method;
        return this;
    }

    public List<TsFragment> getTsFragments() {
        return tsFragments;
    }

    public M3u8VO setTsFragments(List<TsFragment> tsFragments) {
        this.tsFragments = tsFragments;
        return this;
    }

    public void addTsFragment(TsFragment fragment) {
        if (tsFragments == null) {
            tsFragments = new ArrayList<>();
        }
        tsFragments.add(fragment);
    }

    @Override
    public String toString() {
        return "M3u8VO{" +
                "isEncrypted=" + isEncrypted +
                ", method='" + method + '\'' +
                ", tsFragments=" + tsFragments +
                '}';
    }
}
