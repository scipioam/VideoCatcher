package pa.am.video_catcher.catcher.m3u8;

import pa.am.video_catcher.catcher.m3u8.bean.ErrorVO;

import java.util.List;

/**
 * m3u8下载监听器
 *
 * @author Alan Min
 * @since 2021/2/9
 */
public interface DownloadListener {

    /**
     * 单个线程，处理完一个文件时的回调
     * @param finishedFileCount 处理完的文件总数(全局)
     * @param totalFileCount 全局文件总数
     */
    default void onProcessing(int finishedFileCount, int totalFileCount) {
    }

    /**
     * 单个线程，全部处理完的回调
     * @param totalFileCount 全局文件总数
     * @param errorVOList 错误信息列表，如果完全没出错则为null
     */
    default void onFinishedThread(int totalFileCount, List<ErrorVO> errorVOList) {
    }

    /**
     * 全部线程处理完的回调
     */
    default void onTotalThreadFinished() {
    }

}
