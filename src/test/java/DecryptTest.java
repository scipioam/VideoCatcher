import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_crypto.CryptoUtil;
import com.github.ScipioAM.scipio_utils_crypto.mode.SCAlgorithm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/**
 * 解密test
 *
 * @author Alan Scipio
 * @since 2022/5/13
 */
public class DecryptTest {

    @Test
    public void test0() {
        File dir = new File("E:\\temp\\undec_ts");
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        String key = "288C47Cegc9d780E";
        String ivStr = "0x00000000000000000000000000000000";
        try {
            System.out.println("开始解密");
            for (int i = 0; i < files.length; i++) {
                File undecFile = files[i];
                String decFileName = undecFile.getName().replace("_undec", "");
                FileInputStream in = new FileInputStream(undecFile);
                FileOutputStream out = new FileOutputStream("E:\\temp\\dec_ts\\" + decFileName);
                doDecrypt(key, ivStr, in, out);
                System.out.println("进度：" + (i + 1) + "/" + files.length);
            }
            System.out.println("解密成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("解密失败");
        }
    }

    /**
     * 用{@link CryptoUtil}解密
     */
    private void doDecrypt_util(String key, String ivStr, FileInputStream in, FileOutputStream out) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        CryptoUtil cryptoUtil = new CryptoUtil();
        cryptoUtil.decryptStream_symmetric(SCAlgorithm.AES_CBC_PKCS7PADDING, in, out, key,
                (StringUtil.isNotNull(ivStr) ? new IvParameterSpec(ivStr.getBytes()) : null));
    }

    /**
     * 从源头写解密方法
     */
    private void doDecrypt(String key, String ivStr, FileInputStream in, FileOutputStream out) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, paramSpec);

        //开始加密
        int count;
        byte[] cache = new byte[1024];
        try (CipherOutputStream cout = new CipherOutputStream(out, cipher)) {
            while ((count = in.read(cache)) != -1) {
                cout.write(cache, 0, count);
                cout.flush();
            }//end while
        } finally {
            in.close();
            out.close();
        }
    }

    private byte[] getIvBytes(String ivStr) {
        byte[] ivBytes = new byte[16];
        if(StringUtil.isNotNull(ivStr) && ivStr.contains("0x")) {
            String valueStr = ivStr.substring(2);
            char[] chars = valueStr.toCharArray();
            for(int i = (chars.length - 1), j = 0; i > (chars.length - 16); i--, j++) {
                char c = chars[i];
                byte ivByte = Byte.parseByte(String.valueOf(c));
                ivBytes[j] = ivByte;
            }
        } else {
            Arrays.fill(ivBytes, (byte) 0);
        }
        return ivBytes;
    }

    @Test
    public void testGetIvBytes() {
        String ivStr = "0x00000000000000000000000000000012";
        byte[] bytes = getIvBytes(ivStr);
        System.out.println(Arrays.toString(bytes));
    }

}
