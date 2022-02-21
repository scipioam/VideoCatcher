package pa.am.video_catcher;

import com.github.ScipioAM.scipio_utils_javafx.fxml.FxmlView;
import com.github.ScipioAM.scipio_utils_javafx.launch.AbstractApp;
import com.github.ScipioAM.scipio_utils_javafx.launch.AppInitThread;
import com.github.ScipioAM.scipio_utils_javafx.launch.SplashScreen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.controller.MainController;
import pa.am.video_catcher.util.MavenHelper;

import java.util.Map;

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
    private Map<String,String> versionMap;

    public static void main(String[] args) {
        log.info("========== Application launch ==========");
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setProgressBarVisible(false);
        launchApp(App.class,args,splashScreen);
        log.info("========== Application exit ==========");
    }

    //=========================================================================


    @Override
    public void initPrimaryStage(Stage primaryStage) {
        primaryStage.setResizable(false);
    }

    @Override
    public void beforeShowMainView(FxmlView mainView) {
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

//    @Override
//    public String getTitle() {
//
//        return ;
//    }

    public void setPrimaryStageTitle() {
        versionMap = mavenHelper.getVersions();
        primaryStage.setTitle("Video Catcher - v"+versionMap.get(GlobalConst.VERSION_PROJECT));
    }

    //=========================================================================

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

    public Map<String, String> getVersionMap() {
        return versionMap;
    }
}
