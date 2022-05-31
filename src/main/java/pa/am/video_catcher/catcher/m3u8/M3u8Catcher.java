package pa.am.video_catcher.catcher.m3u8;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import pa.am.video_catcher.catcher.m3u8.bean.M3u8VO;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alan Min
 * @since 2021/2/7
 */
public class M3u8Catcher extends M3u8AbstractCatcher {

    //下载子线程数
    private int downloadThreadCount = 30;

    //重试上限
    private int retryLimit = 10;

    //链接连接超时时间（单位：毫秒）
//    private long timeoutMillisecond = 1000L;

    //合并后的文件存储目录
    private String dir = null;

    //合并后的视频文件名称
    private String fileName = null;

    //合并后的视频文件后缀
    private String fileSuffix = ".mp4";

    //发起请求时的ua，为null则按java默认的来
    private String userAgent = null;

    private boolean isDeleteTs = true;//下载并合并后，是否删除ts片段文件

    //处理监听器（下载并解密完一个文件后会回调）
    private DownloadListener downloadListener;

    //解密后的片段(排序规则为按)文件名称进行int排序，文件名称一定是从0开始依此编号
    private final Set<File> finishedFiles = new ConcurrentSkipListSet<>(Comparator.comparingInt(
            o -> Integer.parseInt(o.getName().split("\\.")[0])
    ));

    //=======================================================================================

    /**
     * 执行下载并合并
     * 期间会阻塞catcher本身的线程，直到所有下载子线程完成
     *
     * @param m3u8Url 要下载的m3u8链接
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void doCatch(String m3u8Url) {
        if (StringUtil.isNull(m3u8Url)) {
            throw new IllegalArgumentException("m3u8 url is empty!");
        }
        if (StringUtil.isNull(dir) || StringUtil.isNull(fileName)) {
            throw new IllegalArgumentException("dir or fileName is empty!");
        }
        //创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(downloadThreadCount);
        //发起请求获取下载链接等信息
        log.info("start get m3u8 link`s content");
        M3u8VO vo = getTsContent(m3u8Url, retryLimit);
        Set<String> downloadUrlSet = vo.getTsUrlSet();
        String[] downloadUrlArr = new String[downloadUrlSet.size()];
        downloadUrlArr = downloadUrlSet.toArray(downloadUrlArr);
        log.info("get m3u8 link`s content finished, total file count:" + downloadUrlArr.length);

        //如果生成目录不存在，则创建
//        File dirObj = new File(dir);
        File tempDir = new File(dir + File.separator + "temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        //启动多线程下载
        CountDownLatch latch = buildThreads(vo, downloadUrlArr, threadPool, tempDir);

        log.info("catcher start wait for all download threads to finished themselves job");
        //关闭线程池
        try {
            latch.await();//阻塞直到子线程全部执行完毕
        } catch (Exception e) {
            threadPool.shutdownNow();
            log.error("Got an error when latch await for download threads, {}", e.toString());
            e.printStackTrace();
        }
        threadPool.shutdown();
        log.info("all download threads finished, catcher finish wait");
        if (downloadListener != null) {
            downloadListener.onTotalThreadFinished();
        }

        log.info("start merge ts files");
        //合并文件
        File finalFile = mergeTsFiles(finishedFiles, dir, tempDir, fileName, fileSuffix, isDeleteTs);
        log.info("merge finished");

        log.info("catcher finished, the final download file is: " + finalFile.getAbsolutePath());
    }//end startCatch()

    /**
     * 创建下载子线程，并平均分配任务数
     *
     * @param vo             下载信息对象
     * @param downloadUrlArr 任务数组
     * @param threadPool     线程池
     * @param tempDir        临时下载目录
     * @return 线程同步锁对象
     */
    private CountDownLatch buildThreads(M3u8VO vo, String[] downloadUrlArr, ExecutorService threadPool, File tempDir) {
        CountDownLatch latch = new CountDownLatch(downloadThreadCount);
        int q = downloadUrlArr.length / downloadThreadCount;//系数
        int remainder = downloadUrlArr.length % downloadThreadCount;//余数
        int startIndex = 0;
        int threadIndex = 0;
        //前n个线程，分配到的任务数是(系数+1)，(n=余数)，整除时n为0
        for (int i = 0; i < remainder; i++) {
            System.out.println("thread[" + threadIndex + "]" + " startIndex: " + startIndex);
            M3u8DownloadThread downloadThread = new M3u8DownloadThread("M3u8DownloadThread-" + threadIndex,
                    latch, vo.getEncrypted(), startIndex,
                    (q + 1), downloadUrlArr, tempDir.getPath(), finishedFiles);
            setDownloadThread(downloadThread, vo);
            threadPool.submit(downloadThread);
            startIndex += (q + 1);
            threadIndex++;
        }

        //余下的m个线程，分配到的任务数是q(系数)，(其中m=总线程数-余数)
        for (int j = 0; j < (downloadThreadCount - remainder); j++) {
            System.out.println("thread[" + threadIndex + "]" + " startIndex: " + startIndex);
            M3u8DownloadThread downloadThread = new M3u8DownloadThread("M3u8DownloadThread-" + threadIndex,
                    latch, vo.getEncrypted(), startIndex,
                    q, downloadUrlArr, tempDir.getPath(), finishedFiles);
            setDownloadThread(downloadThread, vo);
            threadPool.submit(downloadThread);
            startIndex += q;
            threadIndex++;
        }
        return latch;
    }

    /**
     * 给下载子线程设置参数
     */
    private void setDownloadThread(M3u8DownloadThread downloadThread, M3u8VO vo) {
        downloadThread.setRetryLimit(retryLimit);
        downloadThread.setUserAgent(userAgent);
        if (vo.getEncrypted()) {
            downloadThread.setEncryptInfo(vo.getKey(), vo.getMethod(), vo.getIvStr());
        }
        if (downloadListener != null) {
            downloadThread.setDownloadListener(downloadListener);
        }
    }

    //=======================================================================================

    public int getDownloadThreadCount() {
        return downloadThreadCount;
    }

    public void setDownloadThreadCount(int downloadThreadCount) {
        this.downloadThreadCount = downloadThreadCount;
    }

    public int getRetryLimit() {
        return retryLimit;
    }

    public void setRetryLimit(int retryLimit) {
        this.retryLimit = retryLimit;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Set<File> getFinishedFiles() {
        return finishedFiles;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public boolean isDeleteTs() {
        return isDeleteTs;
    }

    public void setDeleteTs(boolean deleteTs) {
        isDeleteTs = deleteTs;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
