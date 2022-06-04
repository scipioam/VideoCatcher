package pa.am.video_catcher.catcher.m3u8;

/**
 * m3u8下载后解密失败的异常
 *
 * @author Alan Scipio
 * @since 2022/6/4
 */
public class M3u8DecryptException extends M3u8Exception{
    public M3u8DecryptException() {
    }

    public M3u8DecryptException(String message) {
        super(message);
    }

    public M3u8DecryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public M3u8DecryptException(Throwable cause) {
        super(cause);
    }
}
