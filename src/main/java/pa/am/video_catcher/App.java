package pa.am.video_catcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pa.am.scipioutils.jfoenix.fxml.FxmlView;
import pa.am.scipioutils.jfoenix.launch.AbstractApp;
import pa.am.scipioutils.jfoenix.launch.AppInitThread;
import pa.am.scipioutils.jfoenix.launch.SplashScreen;
import pa.am.video_catcher.controller.MainController;
import pa.am.video_catcher.util.MavenHelper;

/**
 * @author Alan Min
 * @since 2021/2/11
 */
public class App extends AbstractApp {

    private final static Logger log = LogManager.getLogger(App.class);

    private final MavenHelper mavenHelper = new MavenHelper();
    private MainController mainController;
    private String savedLaunchMode;//配置文件里保存的启动哪个模式
    private boolean isSimpleMode;//是否启动极简模式（或者当前是否为极简模式）
    private String version;

    public static void main(String[] args) {
        log.info("========== Application launch ==========");
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setProgressBarVisible(false);
        launchApp(App.class,args,splashScreen);
        log.info("========== Application exit ==========");
    }

    @Override
    public void beforeShowMainView(FxmlView mainView) {
        version = mavenHelper.getVersion();

        primaryStage.setResizable(false);
        primaryStage.setTitle("Video Catcher - v"+version);
        mainController = (MainController) mainView.getController();
        mainController.setApp(this);
        mainController.setThreadPool(threadPool);
        mainController.initContentPane();
    }

    @Override
    public void stop() {
        mainController.onStop();
    }

    @Override
    public AppInitThread getInitThread(SplashScreen splashScreen) {
        return new InitThread(this,this,splashScreen);
    }

    @Override
    public String getMainViewPath() {
        return "/view/main.fxml";
    }

    @Override
    public String getIconPath() {
        return "/img/logo.png";
    }

    public boolean isSimpleMode() {
        return isSimpleMode;
    }

    public void setSimpleMode(boolean simpleMode) {
        isSimpleMode = simpleMode;
    }

    public String getSavedLaunchMode() {
        return savedLaunchMode;
    }

    public void setSavedLaunchMode(String savedLaunchMode) {
        this.savedLaunchMode = savedLaunchMode;
    }

    public String getVersion() {
        return version;
    }
}
