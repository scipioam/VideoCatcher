package pa.am.video_catcher.catcher.m3u8.bean;

/**
 * @author Alan Scipio
 * @since 2022/8/6
 */
public class TsFragment {

    //ts片段是否被加密
    private Boolean isEncrypted = false;

    private TsKey tsKey = new TsKey();

    //ts片段的url
    private String tsUrl;

    public TsKey getTsKey() {
        return tsKey;
    }

    public TsFragment setTsKey(TsKey tsKey) {
        this.tsKey = tsKey;
        return this;
    }

    public TsFragment clearTsKey(TsKey tsKey) {
        tsKey.setKeyContent(null);
        tsKey.setKeyUrl(null);
        tsKey.setIvStr(null);
        tsKey.setMethod(null);
        return this;
    }

    public Boolean getEncrypted() {
        return isEncrypted;
    }

    public TsFragment setEncrypted(Boolean encrypted) {
        isEncrypted = encrypted;
        return this;
    }

    public String getKeyContent() {
        return tsKey.getKeyContent();
    }

    public TsFragment setKeyContent(String keyContent) {
        tsKey.setKeyContent(keyContent);
        return this;
    }

    public String getKeyUrl() {
        return tsKey.getKeyUrl();
    }

    public TsFragment setKeyUrl(String keyUrl) {
        tsKey.setKeyUrl(keyUrl);
        return this;
    }

    public String getMethod() {
        return tsKey.getMethod();
    }

    public TsFragment setMethod(String method) {
        tsKey.setMethod(method);
        return this;
    }

    public String getIvStr() {
        return tsKey.getIvStr();
    }

    public TsFragment setIvStr(String ivStr) {
        tsKey.setIvStr(ivStr);
        return this;
    }

    public String getTsUrl() {
        return tsUrl;
    }

    public TsFragment setTsUrl(String tsUrl) {
        this.tsUrl = tsUrl;
        return this;
    }
}
