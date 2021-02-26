package pa.am.video_catcher.catcher.bilibili;

import pa.am.scipioutils.common.StringUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * B站工具类
 * av-bv的转换参考：https://www.bilibili.com/read/cv5341859/
 * @author Alan Min
 * @since 2021/2/23
 */
public class BilibiliUtil {

    //字符密码表
    private final char[] keyTable = new char[] {
            'f','Z','o','d','R','9','X','Q','D','S','U','m','2','1','y','C','k','r','6','z','B','q','i','v','e','Y','a','h','8','b','t','4','x','s','W','p','H','n','J','E','7','j','L','5','V','G','3','g','u','M','T','K','N','P','A','w','c','F'
    };

    //反过来的字符密码表（字符为key，下标为value）
    private final Map<Character,BigInteger> reverseKeyTable = new HashMap<>();

    //58的幂运算时的对照表
    private final int[] powTable = new int[] {
      6,2,4,8,5,9,3,7,1,0
    };

    public BilibiliUtil() {
        for(int i=0; i< keyTable.length; i++) {
            reverseKeyTable.put(keyTable[i],new BigInteger(i+""));
        }
    }

    /**
     * BV号转AV号
     * @param bvid BV号（带BV前缀）
     * @return AV号
     */
    public String bvToAv(String bvid) {
        char[] bvArr = bvid.substring(2).toCharArray();
        BigInteger intId = new BigInteger("0");
        int i = 0;
        for (char c : bvArr) {
            BigInteger index = reverseKeyTable.get(c);
            BigInteger temp = new BigInteger("58").pow(powTable[i]).multiply(index);
            intId = intId.add(temp);
            i++;
        }
        intId = intId.subtract(new BigInteger("100618342136696320"));
        intId = intId.xor(new BigInteger("177451812"));
        return intId.toString();
    }

    /**
     * AV号转BV号
     * @param avid AV号（不带av前缀）
     * @return BV号
     */
    public String avToBv(String avid) {
        if(!StringUtil.isIntNumeric(avid)) {
            throw new BilibiliException("avid is not a integral string");
        }

        StringBuilder bvid = new StringBuilder();
        BigInteger av = new BigInteger(avid);
        av = av.xor(new BigInteger("177451812"));
        av = av.add(new BigInteger("100618342136696320"));
        for(int i : powTable) {
            BigInteger temp0 = av.divide(new BigInteger("58").pow(i));
            BigInteger temp1 = temp0.mod(new BigInteger("58"));
            bvid.append(keyTable[temp1.intValue()]);
        }
        return bvid.toString();
    }

}
