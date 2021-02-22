package pa.am.video_catcher.catcher.bean;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * m3u8下载时的信息bean
 * @author Alan Min
 * @since 2021/2/8
 */
public class M3u8VO {

    //ts片段是否被加密
    private Boolean isEncrypted = false;

    //加解密的密钥
    private String key;

    //密钥的url
    private String keyUrl;

    //加解密算法名称
    private String method;

    //加解密可能要用到的IV
    private String ivStr;

    //所有ts片段下载链接
    private Set<String> tsUrlSet;

    public Boolean getEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyUrl() {
        return keyUrl;
    }

    public void setKeyUrl(String keyUrl) {
        this.keyUrl = keyUrl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIvStr() {
        return ivStr;
    }

    public void setIvStr(String ivStr) {
        this.ivStr = ivStr;
    }

    public Set<String> getTsUrlSet() {
        return tsUrlSet;
    }

    public void setTsUrlSet(LinkedHashSet<String> tsUrlSet) {
        this.tsUrlSet = tsUrlSet;
    }

    public void addTsUrl(String tsUrl) {
        if(tsUrlSet==null) {
            tsUrlSet = new LinkedHashSet<>();
        }
        tsUrlSet.add(tsUrl);
    }

    @Override
    public String toString() {
        return "M3u8VO{" +
                "isEncrypted=" + isEncrypted +
                ", key='" + key + '\'' +
                ", keyUrl='" + keyUrl + '\'' +
                ", method='" + method + '\'' +
                ", ivStr='" + ivStr + '\'' +
                ", tsUrlSet=" + tsUrlSet +
                '}';
    }

}
