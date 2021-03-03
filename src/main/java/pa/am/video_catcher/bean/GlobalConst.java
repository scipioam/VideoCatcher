package pa.am.video_catcher.bean;

/**
 * Class: GlobalConst
 * Description:
 * Author: Alan Min
 * Create Date: 2020/9/14
 */
public class GlobalConst {

    //***************************** 本程序相关 *****************************
    public static final String CONFIG_FILE_PATH = "app.properties";//配置文件名称（及相对路径）
    public static final String CONFIG_LAUNCH_MODE = "app.launch.mode";//启动模式的key
    public static final String CONFIG_LAUNCH_MODE_SIMPLE = "0";//极简模式
    public static final String CONFIG_LAUNCH_MODE_PRO = "1";//高级模式

    public static final long THREAD_KEEP_TIME = 2000L; //耗时子线程执行时间不低于2秒

    public static final String FFMPEG_PATH = ".\\tools\\ffmpeg.exe";//ffmpeg路径
    public static final String YDL_PATH = ".\\tools\\youtube-dl.exe";//youtube-dl路径

    public static final String VERSION_PROJECT = "project.version";//项目版本号
    public static final String VERSION_YOUTUBE_DL = "youtube-dl.version";//youtube-dl版本号
    public static final String VERSION_FFMPEG = "ffmpeg.version";//ffmpeg版本号

    //***************************** 爬取网站相关 *****************************
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36";//ua的值\

}
