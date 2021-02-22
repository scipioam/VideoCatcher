package pa.am.video_catcher.util;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * @author Alan Min
 * @since 2021/2/22
 */
public class DesktopUtil {

    /**
     * 获取桌面文件夹对象
     */
    public static File getDesktopFile() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getHomeDirectory();
    }

}
