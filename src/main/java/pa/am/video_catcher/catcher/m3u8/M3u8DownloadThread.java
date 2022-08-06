package pa.am.video_catcher.catcher.m3u8;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_crypto.CryptoUtil;
import com.github.ScipioAM.scipio_utils_crypto.mode.SCAlgorithm;
import com.github.ScipioAM.scipio_utils_net.http.HttpUtil;
import com.github.ScipioAM.scipio_utils_net.http.bean.ResponseResult;
import com.github.ScipioAM.scipio_utils_net.http.common.ResponseDataMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import pa.am.video_catcher.catcher.m3u8.bean.ErrorVO;
import pa.am.video_catcher.catcher.m3u8.bean.TsFragment;
import pa.am.video_catcher.catcher.m3u8.bean.TsKey;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * ts片段的下载子线程
 *
 * @author Alan Min
 * @since 2021/2/8
 */
public class M3u8DownloadThread implements Runnable {

    private final Logger log = LogManager.getLogger(M3u8DownloadThread.class);

    private final String TS_FILE_SUFFIX = ".ts";//下载的ts文件后缀（已解密）

    private final String threadName;
    private final CountDownLatch latch;
    //在总url列表中的起始下标
    private final int startIndex;
    //要处理的数量长度
    private final int processLength;
    //下载清单
    private final List<TsFragment> tsFragments;
    //下载路径
    private final String tempDir;
    //已完成的文件列表（线程安全）
    private final Set<File> finishedFileSet;
    //从属的catcher
    private final M3u8Catcher catcher;

    //发起请求时的ua，为null则按java默认的来
    private String userAgent = null;
    //重试的上限
    private int retryLimit = 2;
    //错误信息列表
    private List<ErrorVO> errorVOList;
    //下载监听器
    private DownloadListener downloadListener;

    private final HttpUtil httpUtil = new HttpUtil();
    private CryptoUtil cryptoUtil;

    public M3u8DownloadThread(String threadName, CountDownLatch latch, int startIndex, int processLength, List<TsFragment> tsFragments, String tempDir, Set<File> finishedFileSet, M3u8Catcher catcher) {
        this.threadName = threadName;
        this.latch = latch;
        this.startIndex = startIndex;
        this.processLength = processLength;
        this.tsFragments = tsFragments;
        this.tempDir = tempDir;
        this.finishedFileSet = finishedFileSet;
        this.catcher = catcher;
    }

    public M3u8DownloadThread(CountDownLatch latch, int startIndex, int processLength, List<TsFragment> tsFragments, String dir, Set<File> finishedFileSet, M3u8Catcher catcher) {
        this(null, latch, startIndex, processLength, tsFragments, dir, finishedFileSet, catcher);
    }

    @Override
    public void run() {
        if (StringUtil.isNotNull(threadName)) {
            Thread.currentThread().setName(threadName);
        }

        int endLimit = startIndex + processLength;
        int failCount = 0;
        for (int i = startIndex; i < endLimit; i++) {
            TsFragment fragment = tsFragments.get(i);

            File undecTsFile = download(fragment, i);
            if (undecTsFile != null) {
                boolean success = decryptFile(undecTsFile, i, fragment);
                if (!success) {
                    failCount++;
                }
            }

            if (downloadListener != null) {
                downloadListener.onProcessing(finishedFileSet.size(), tsFragments.size());
            }
        }//end for

        if (downloadListener != null) {
            downloadListener.onFinishedThread(tsFragments.size(), errorVOList);
        }

        latch.countDown();

        //失败线程的计数加1
        if (failCount > 0) {
            catcher.getFailThreadCount().increment();
        }

        log.info("M3u8 download thread finished,startIndex:{}, total:{}, success:{}. fail:{}", startIndex, processLength, (processLength - failCount), failCount);
    }//end run()

    /**
     * 下载单个ts文件
     *
     * @param fragment ts片段
     * @param index    在总列表中的下标（ts文件的总序号）
     * @return 已下载好但未解密的ts文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File download(TsFragment fragment, int index) {
        File undecTsFile = null;
        InputStream in = null;
        FileOutputStream out = null;
        int count = 1;//重试计数
        String errMsg = null;
        Exception exception = null;

        if (StringUtil.isNotNull(userAgent)) {
            httpUtil.setUserAgent(userAgent);
        }

        while (count <= retryLimit) {
            try {
                //发起请求
                ResponseResult response = httpUtil.get(fragment.getTsUrl(), ResponseDataMode.STREAM_ONLY);
                int responseCode = response.getResponseCode();
                if (responseCode <= -1) {
                    log.warn("[{}/{}]Http error, response code: {}, index: {}, url: {}", count, retryLimit, responseCode, index, fragment.getTsUrl());
                    errMsg = "Http error, responseCode:" + responseCode + ", url:" + fragment.getTsUrl();
                    count++;
                    continue;
                }

                in = response.getResponseStream();
                if (in == null) {
                    log.error("[{}/{}]InputStream from response obj is null!", count, retryLimit);
                    errMsg = "InputStream from response obj is null!";
                    count++;
                    continue;
                }

                String tsFileName = (fragment.getEncrypted() ? index + "_undec" : index + "");
                undecTsFile = new File(tempDir + File.separator + tsFileName + TS_FILE_SUFFIX);
                if (undecTsFile.exists())
                    undecTsFile.delete(); //未解密的ts片段，如果存在，则删除
                undecTsFile.createNewFile();
                out = new FileOutputStream(undecTsFile);

                //将未解密的ts片段写入文件
                int len;
                byte[] bytes = new byte[1024];
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                }
                out.flush();
                break;
            } catch (Exception thisE) {
                errMsg = "Got an error when download";
                exception = thisE;
                log.error("[{}]Got an error when download: {}", count, thisE.toString());
                thisE.printStackTrace();
                count++;
            } finally {
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (Exception e) {
                    log.error("Got an error when close stream, {}", e.toString());
                    e.printStackTrace();
                }
            }//end finally
        }//end outside while
        if (count >= retryLimit) {
            log.error("Download failed at the end, file index[{}], ts url[{}]", index, fragment.getTsUrl());
            addNewError(index, errMsg, exception);
            undecTsFile = null;
        }
        return undecTsFile;
    }//end download()

    /**
     * 解密处理
     *
     * @param undecTsFile 未解密的ts文件
     * @param index       在总列表中的下标（ts文件的总序号）
     * @return 解密是否成功。true代表成功
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean decryptFile(File undecTsFile, int index, TsFragment fragment) {
        File decTsFile;
        String method = fragment.getMethod();
        String keyContent = fragment.getKeyContent();
        //需要解密
        if (fragment.getEncrypted()) {
            //基本检查
            if (StringUtil.isNull(method) || StringUtil.isNull(keyContent)) {
                log.error("method or keyContent is null when decryptFile, file index: {}", index);
                addNewError(index, "method or keyContent is null when decryptFile", null);
                return false;
            }
            //检查算法是否超出预期
            if (!method.contains("AES")) {
                log.error("Ts file`s encrypt algorithm is not an expect algorithm! this algorithm is: {}", method);
                addNewError(index, "Ts file`s encrypt algorithm is not an expect algorithm! this algorithm is: " + method, null);
                return false;
            }

            decTsFile = new File(tempDir + File.separator + index + TS_FILE_SUFFIX);
            try {
                if (decTsFile.exists())
                    decTsFile.delete();
                decTsFile.createNewFile();
            } catch (Exception e) {
                log.error("Recreate decrypt file failed, index:{}, {}", index, e.toString());
                e.printStackTrace();
                return false;
            }

            if (cryptoUtil == null) {
                cryptoUtil = new CryptoUtil();
            }

//            FileInputStream in;
//            FileOutputStream out;
            try {
//                in = new FileInputStream(undecTsFile);
//                out = new FileOutputStream(decTsFile);
                //解密并输出到文件(该解密方法里已自行关闭了流)
//                cryptoUtil.decryptStream_symmetric(SCAlgorithm.AES, in, out, key,
//                        (StringUtil.isNotNull(ivStr) ? new IvParameterSpec(ivStr.getBytes()) : null));
                doDecryptFile(undecTsFile, decTsFile, true, fragment.getTsKey());
            } catch (Exception e) {
//                log.error("Got an error when decrypt, index:{}, {}", index, e.toString());
                addNewError(index, "Got an error when decrypt", e);
                e.printStackTrace();
                return false;
            }
        }
        //不需要解密
        else {
            decTsFile = undecTsFile;
        }

        finishedFileSet.add(decTsFile);
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void doDecryptFile(File undecTsFile, File decTsFile, boolean isDeleteUndecFile, TsKey tsKey) throws Exception {
        String ivStr = tsKey.getIvStr();
        String keyContent = tsKey.getKeyContent();
        byte[] keyBytes = keyContent.getBytes(StandardCharsets.UTF_8);
        //根据iv字符串获取iv字节数组
        byte[] ivBytes = new byte[16];
        if (StringUtil.isNotNull(ivStr) && ivStr.contains("0x")) {
            String valueStr = ivStr.substring(2);
            char[] chars = valueStr.toCharArray();
            for (int i = 0, j = 0; i < ivBytes.length; i++) {
                ivBytes[i] = (byte) (hexChar2Byte(chars[j++]) << 4 | hexChar2Byte(chars[j++]));
            }
        } else {
            Arrays.fill(ivBytes, (byte) 0);
        }

        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(SCAlgorithm.AES_CBC_PKCS7PADDING.getName());
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, SCAlgorithm.AES.getName());
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, paramSpec);

        //开始加密
        int count;
        byte[] cache = new byte[1024];
        try (FileInputStream in = new FileInputStream(undecTsFile); FileOutputStream out = new FileOutputStream(decTsFile); CipherOutputStream cout = new CipherOutputStream(out, cipher)) {
            while ((count = in.read(cache)) != -1) {
                cout.write(cache, 0, count);
                cout.flush();
            }
            if (isDeleteUndecFile) {
                undecTsFile.delete();//删除未解密的文件
            }
        }
    }

    private int hexChar2Byte(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 0x0A);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 0x0a);
        throw new RuntimeException("Invalid hex char '" + c + "'");
    }

    //=================================================================================

    /**
     * 添加一个新的错误信息到list里去
     *
     * @param index 出错的文件在总数组里的下标
     * @param msg   出错信息
     * @param e     异常对象（可能为null）
     */
    private void addNewError(int index, String msg, Exception e) {
        if (errorVOList == null) {
            errorVOList = new ArrayList<>();
        }
        ErrorVO vo = new ErrorVO(index, msg, Thread.currentThread().getName(), e);
        errorVOList.add(vo);
        catcher.getErrorVOList().add(vo);
    }

    public void setRetryLimit(int retryLimit) {
        this.retryLimit = retryLimit;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
