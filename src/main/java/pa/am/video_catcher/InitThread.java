package pa.am.video_catcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pa.am.scipioutils.common.PropertiesHelper;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.jfoenix.launch.AbstractApp;
import pa.am.scipioutils.jfoenix.launch.AppInitThread;
import pa.am.scipioutils.jfoenix.launch.LaunchListener;
import pa.am.scipioutils.jfoenix.launch.SplashScreen;
import pa.am.video_catcher.bean.GlobalConst;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 程序启动初始化子线程
 * @author Alan Min
 * @since 2021/2/22
 */
public class InitThread extends AppInitThread {

    private final Logger log = LogManager.getLogger(InitThread.class);

    public InitThread(AbstractApp app, LaunchListener launchListener, SplashScreen splashScreen) {
        super(app, launchListener, splashScreen);
    }

    @Override
    public void init(AbstractApp abstractApp) {
        App thisApp = (App) abstractApp;
        PropertiesHelper helper = new PropertiesHelper(GlobalConst.CONFIG_FILE_PATH);
        try {
            //读取配置文件
            Properties properties = helper.getConfig();
            boolean isSimpleMode;
            //文件不存在
            if(properties==null) {
                isSimpleMode = firstInit(helper);
            }
            //文件存在并已读取
            else {
                isSimpleMode = commonInit(properties);
            }
            thisApp.setSimpleMode(isSimpleMode);
            thisApp.setSavedLaunchMode(isSimpleMode ? GlobalConst.CONFIG_LAUNCH_MODE_SIMPLE : GlobalConst.CONFIG_LAUNCH_MODE_PRO);
        }catch (Exception e) {
            log.error("Got an error when read config file, Exception: {}",e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 配置文件不存在时，完全的第一次初始化
     * @return 启动的是极简模式还是高级模式，返回true代表启动的是极简模式
     * @throws IOException 创建配置文件失败
     */
    private boolean firstInit(PropertiesHelper helper) throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put(GlobalConst.CONFIG_LAUNCH_MODE,GlobalConst.CONFIG_LAUNCH_MODE_SIMPLE);
        helper.saveConfig(config,null);
        log.info("Config file created");
        return true;
    }

    /**
     * 配置文件存在时，常规的一次初始化
     * @param properties 读取的配置项
     * @return 启动的是极简模式还是高级模式，返回true代表启动的是极简模式
     */
    private boolean commonInit(Properties properties) {
        boolean isSimpleMode;
        String launchMode = properties.getProperty(GlobalConst.CONFIG_LAUNCH_MODE);
        //上次导入的文件
        if(StringUtil.isNotNull(launchMode)) {
            isSimpleMode = launchMode.equals(GlobalConst.CONFIG_LAUNCH_MODE_SIMPLE);
            log.info("Launch mode has been read from config: mode[{}]",launchMode);
        }
        else {
            isSimpleMode = true;
            log.info("Launch mode is empty when read from config");
        }
        return isSimpleMode;
    }

}
