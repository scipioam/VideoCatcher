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
     * @param isVideo 是否在下载视频（为false则是在下载音频）
     * @param downloadedBytes 已下载的字节数
     * @param totalBytes 总字节数
     */
    void onDownload(boolean isVideo, long downloadedBytes, long totalBytes);

    /**
     * 开始合并音视频
     */
    void onStartCombine();

    /**
     * 下载完成时
     * @param totalBytes 总字节数
     * @param downloadedFile 下载的文件
     * @param media 下载的多媒体信息
     */
    void onDownloadFinished(long totalBytes, File downloadedFile, Media media);

}
