package pa.am.video_catcher.catcher.m3u8.bean;

import com.github.ScipioAM.scipio_utils_common.StringUtil;

/**
 * @author Alan Scipio
 * @since 2022/8/6
 */
public class TsKey {

    //加解密的密钥
    private String keyContent;

    //密钥的url
    private String keyUrl;

    //加解密算法名称
    private String method;

    //加解密可能要用到的IV
    private String ivStr;

    /**
     * 比较是否为实质性的同一个key
     *
     * @return true：是同一个
     */
    public boolean sameKey(TsKey anotherKey) {
        if (anotherKey == null) {
            return false;
        }

        if (StringUtil.isNotNull(keyContent) && keyContent.equals(anotherKey.getKeyContent())) {
            return true;
        } else if (keyUrl.equals(anotherKey.getKeyUrl())) {
            return true;
        } else {
            return false;
        }
    }

    public String getKeyContent() {
        return keyContent;
    }

    public TsKey setKeyContent(String keyContent) {
        this.keyContent = keyContent;
        return this;
    }

    public String getKeyUrl() {
        return keyUrl;
    }

    public TsKey setKeyUrl(String keyUrl) {
        this.keyUrl = keyUrl;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public TsKey setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getIvStr() {
        return ivStr;
    }

    public TsKey setIvStr(String ivStr) {
        this.ivStr = ivStr;
        return this;
    }
}
