package pa.am.video_catcher.catcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.crypto.CryptoUtil;
import pa.am.scipioutils.crypto.mode.SCAlgorithm;
import pa.am.scipioutils.net.http.HttpUtil;
import pa.am.scipioutils.net.http.common.Response;
import pa.am.scipioutils.net.http.common.ResponseDataMode;
import pa.am.video_catcher.catcher.bean.ErrorVO;

import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * ts片段的下载子线程
 * @author Alan Min
 * @since 2021/2/8
 */
public class M3u8DownloadThread implements Runnable{

    private final Logger log = LogManager.getLogger(M3u8DownloadThread.class);

    private final String TS_FILE_SUFFIX = ".ts";//下载的ts文件后缀（已解密）

    private final String threadName;
    private final CountDownLatch latch;
    //ts片段是否被加密了
    private final boolean isEncrypted;
    //在总url列表中的起始下标
    private final int startIndex;
    //要处理的数量长度
    private final int processLength;
    //下载清单
    private final String[] tsUrlArr;
    //下载路径
    private final String dir;
    //已完成的文件列表（线程安全）
    private final Set<File> finishedFileSet;

    //加解密密钥
    private String key = null;
    //加解密要用的参数
    private String ivStr = null;
    //加解密算法名称
    private String method = null;
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

    public M3u8DownloadThread(String threadName, CountDownLatch latch, boolean isEncrypted, int startIndex, int processLength, String[] tsUrlArr, String dir, Set<File> finishedFileSet) {
        this.threadName = threadName;
        this.latch = latch;
        this.isEncrypted = isEncrypted;
        this.startIndex = startIndex;
        this.processLength = processLength;
        this.tsUrlArr = tsUrlArr;
        this.dir = dir;
        this.finishedFileSet = finishedFileSet;
    }

    public M3u8DownloadThread(CountDownLatch latch, boolean isEncrypted, int startIndex, int processLength, String[] tsUrlArr, String dir, Set<File> finishedFileSet) {
        this(null,latch,isEncrypted,startIndex,processLength,tsUrlArr,dir,finishedFileSet);
    }

    @Override
    public void run() {
        if(StringUtil.isNotNull(threadName)) {
            Thread.currentThread().setName(threadName);
        }

        int endLimit = startIndex + processLength;
        for(int i=startIndex; i<endLimit; i++) {
            File undecTsFile = download(tsUrlArr[i], i);
            if(undecTsFile!=null) {
                decryptFile(undecTsFile,i);
            }

            if(downloadListener!=null) {
                downloadListener.onProcessing(finishedFileSet.size(),tsUrlArr.length);
            }
        }//end for

        if(downloadListener!=null) {
            downloadListener.onFinishedThread(tsUrlArr.length,errorVOList);
        }

        latch.countDown();
    }//end run()

    /**
     * 下载单个ts文件
     * @param url ts文件的url
     * @param index 在总列表中的下标（ts文件的总序号）
     * @return 已下载好但未解密的ts文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File download(String url, int index) {
        File undecTsFile = null;
        InputStream in = null;
        FileOutputStream out = null;
        int count = 0;//重试计数
        String errMsg = null;
        Exception exception = null;

        if(StringUtil.isNotNull(userAgent)) {
            httpUtil.setUserAgent(userAgent);
        }

        while (count < retryLimit) {
            try {
                //发起请求
                Response response = httpUtil.get(url, ResponseDataMode.STREAM_ONLY);
                int responseCode = response.getResponseCode();
                if(responseCode<=-1) {
                    log.warn("[{}]Http error, response code: {}, index: {}, url: {}", count, responseCode, index, url);
                    errMsg = "Http error, responseCode:"+responseCode+", url:"+url;
                    count++;
                    continue;
                }

                in = response.getResponseStream();
                if(in==null) {
                    log.error("[{}]InputStream from response obj is null!",count);
                    errMsg = "InputStream from response obj is null!";
                    count++;
                    continue;
                }

                String tsFileName = (isEncrypted ? index+"_undec" : index+"");
                undecTsFile = new File(dir + File.separator + tsFileName + TS_FILE_SUFFIX);
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
            }catch (Exception thisE) {
                errMsg = "Got an error when download";
                exception = thisE;
                log.error("[{}]Got an error when download: {}",count,thisE.toString());
                thisE.printStackTrace();
                count++;
            }finally {
                try {
                    if(in!=null)
                        in.close();
                    if(out!=null)
                        out.close();
                }catch (Exception e) {
                    log.error("Got an error when close stream, {}",e.toString());
                    e.printStackTrace();
                }
            }//end finally
        }//end outside while
        if(count>=retryLimit) {
            log.warn("Download failed at the end, file index[{}], ts url[{}]",index,url);
            addNewError(index,errMsg,exception);
            undecTsFile = null;
        }
        return undecTsFile;
    }//end download()

    /**
     * 解密处理
     * @param undecTsFile 未解密的ts文件
     * @param index 在总列表中的下标（ts文件的总序号）
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void decryptFile(File undecTsFile, int index) {
        File decTsFile;
        //需要解密
        if(isEncrypted) {
            //基本检查
            if(method==null || key==null) {
                log.error("method or key is null when decryptFile, file index: {}",index);
                addNewError(index,"method or key is null when decryptFile",null);
                return;
            }
            //检查算法是否超出预期
            if (!method.contains("AES")) {
                log.error("Ts file`s encrypt algorithm is not an expect algorithm! this algorithm is: {}",method);
                addNewError(index,"Ts file`s encrypt algorithm is not an expect algorithm! this algorithm is: "+method,null);
                return;
            }

            decTsFile = new File(dir + File.separator + index + TS_FILE_SUFFIX);
            try {
                if(decTsFile.exists())
                    decTsFile.delete();
                decTsFile.createNewFile();
            }catch (Exception e) {
                log.error("Recreate decrypt file failed, index:{}, {}",index,e.toString());
                e.printStackTrace();
                return;
            }

            if(cryptoUtil==null) {
                cryptoUtil=new CryptoUtil();
            }

            FileInputStream in;
            FileOutputStream out;
            try {
                in = new FileInputStream(undecTsFile);
                out = new FileOutputStream(decTsFile);
                //解密并输出到文件(该解密方法里已自行关闭了流)
                cryptoUtil.decryptStream_symmetric(SCAlgorithm.AES_CBC_PKCS7PADDING,in,out,null,
                        (StringUtil.isNotNull(ivStr)?new IvParameterSpec(ivStr.getBytes()):null) );
                undecTsFile.delete();//删除未解密的文件
            }catch (Exception e) {
                log.error("Got an error when decrypt, index:{}, {}",index,e.toString());
                addNewError(index,"Got an error when decrypt",e);
                e.printStackTrace();
            }
        }//end outside if
        //不需要解密
        else {
            decTsFile = undecTsFile;
        }

        finishedFileSet.add(decTsFile);
    }//end decryptFile()

    //=================================================================================

    /**
     * 添加一个新的错误信息到list里去
     * @param index 出错的文件在总数组里的下标
     * @param msg 出错信息
     * @param e 异常对象（可能为null）
     */
    private void addNewError(int index, String msg, Exception e) {
        if(errorVOList==null) {
            errorVOList = new ArrayList<>();
        }
        ErrorVO vo = new ErrorVO(index,msg,Thread.currentThread().getName(),e);
        errorVOList.add(vo);
    }

    public void setEncryptInfo(String key, String method, String ivStr) {
        setKey(key);
        setMethod(method);
        setIvStr(ivStr);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setIvStr(String ivStr) {
        this.ivStr = ivStr;
    }

    public void setMethod(String method) {
        this.method = method;
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
