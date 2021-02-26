package pa.am.video_catcher.catcher.bilibili;

import pa.am.video_catcher.catcher.bilibili.bean.media_play.Media;

import java.io.File;

/**
 * @author Alan Min
 * @since 2021/2/25
 */
public interface DownloadListener {

    /**
     * 下载时
     * @param downloadedBytes 已下载的字节数
     * @param totalBytes 总字节数
     */
    void onProcess(long downloadedBytes, long totalBytes);

    /**
     * 下载完成时
     * @param totalBytes 总字节数
     * @param downloadedFile 下载的文件
     * @param media 下载的多媒体信息
     */
    void onFinished(long totalBytes, File downloadedFile, Media media);

}
