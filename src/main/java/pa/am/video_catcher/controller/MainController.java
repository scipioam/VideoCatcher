package pa.am.video_catcher.controller;

import com.jfoenix.controls.JFXTabPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pa.am.scipioutils.jfoenix.fxml.BaseController;
import pa.am.scipioutils.jfoenix.fxml.FXMLLoadHelper;
import pa.am.scipioutils.jfoenix.fxml.FxmlView;
import pa.am.video_catcher.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * 主界面controller
 * @author Alan Min
 * @since 2021/2/11
 */
public class MainController extends BaseController {

    private final Logger log = LogManager.getLogger(MainController.class);
    private App app;
    private ExecutorService threadPool;

    private FxmlView videoDownView;
    private FxmlView m3u8View;
    private FxmlView biliView;
    private FxmlView simpleModeView;
    private FxmlView launchSettingView;

    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private MenuItem menuItem_switchMode;

    private final JFXTabPane tabPane = new JFXTabPane();
    private final Tab tab_videoDown = new Tab("通用视频下载");
    private final Tab tab_m3u8 = new Tab("m3u8下载");
    private final Tab tab_bili = new Tab("B站下载");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getTabs().addAll(tab_videoDown,tab_m3u8,tab_bili);
        simpleModeView = buildView("/view/simple_mode.fxml",null);
        videoDownView = buildView("/view/video_down.fxml",tab_videoDown);
        m3u8View = buildView("/view/m3u8.fxml",tab_m3u8);
        biliView = buildView("/view/bili_down.fxml",tab_bili);
    }

    public void initContentPane() {
        boolean isSimpleMode = app.isSimpleMode();
        //极简模式
        if(isSimpleMode) {
            Parent view = simpleModeView.getView();
            AnchorPane.setLeftAnchor(view,0.0);
            AnchorPane.setRightAnchor(view,0.0);
            AnchorPane.setTopAnchor(view,30.0);
            AnchorPane.setBottomAnchor(view,0.0);
            contentPane.getChildren().add(view);
            menuItem_switchMode.setText("进入高级模式");
        }
        //高级模式
        else {
            tab_videoDown.setContent(videoDownView.getView());
            tab_m3u8.setContent(m3u8View.getView());
            tab_bili.setContent(biliView.getView());
            AnchorPane.setLeftAnchor(tabPane,0.0);
            AnchorPane.setRightAnchor(tabPane,0.0);
            AnchorPane.setTopAnchor(tabPane,30.0);
            AnchorPane.setBottomAnchor(tabPane,0.0);
            contentPane.getChildren().add(tabPane);
            menuItem_switchMode.setText("进入极简模式");
        }
    }

    @Override
    public void onStop() {
        videoDownView.getController().onStop();
        biliView.getController().onStop();
        threadPool.shutdownNow();
    }

    //=========================================================================

    /**
     * 菜单：切换另一个模式
     */
    @FXML
    private void menu_switchMode() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.hide();
        //当前是极简模式，进入高级模式
        if(app.isSimpleMode()) {
            AnchorPane.setLeftAnchor(tabPane,0.0);
            AnchorPane.setRightAnchor(tabPane,0.0);
            AnchorPane.setTopAnchor(tabPane,30.0);
            AnchorPane.setBottomAnchor(tabPane,0.0);
            contentPane.getChildren().remove(1);
            contentPane.getChildren().add(tabPane);
            menuItem_switchMode.setText("进入极简模式");
            app.setSimpleMode(false);
        }
        //当前是高级模式，进入极简模式
        else {
            Parent view = simpleModeView.getView();
            AnchorPane.setLeftAnchor(view,0.0);
            AnchorPane.setRightAnchor(view,0.0);
            AnchorPane.setTopAnchor(view,30.0);
            AnchorPane.setBottomAnchor(view,0.0);
            contentPane.getChildren().remove(1);
            contentPane.getChildren().add(view);
            menuItem_switchMode.setText("进入高级模式");
            app.setSimpleMode(true);
        }
        stage.show();
    }

    /**
     * 菜单：设置启动界面
     */
    @FXML
    private void menu_setLaunchPane() {
        if(launchSettingView==null) {
            launchSettingView = FxmlView.showView(launchSettingView,"/view/launch_setting.fxml",
                    "启动界面设置", rootPane.getScene().getWindow());
            LaunchSettingController controller = (LaunchSettingController) launchSettingView.getController();
            controller.setThisStage(launchSettingView.getStage());
            controller.setMainRootPane(rootPane);
            controller.setApp(app);
        }
        else{
            launchSettingView.getStage().show();
        }
    }

    /**
     * 菜单：关于
     */
    @FXML
    private void menu_about() {

    }

    /**
     * 菜单：退出程序
     */
    @FXML
    private void menu_exit() {
        Platform.exit();
    }

    //=========================================================================

    /**
     * 创建界面
     * @param fxmlPath 子界面fxml的路径
     * @param tab 子界面所属的tab
     * @return 子界面对象
     */
    private FxmlView buildView(String fxmlPath, Tab tab) {
        FxmlView view = null;
        FXMLLoadHelper helper = new FXMLLoadHelper();
        try {
            view = helper.load(fxmlPath);
            if(view!=null) {
                AbstractPageController controller = (AbstractPageController) view.getController();
                controller.setMainController(this);
                controller.setRootPane(rootPane);
                if(tab!=null) {
                    tab.setContent(view.getView());
                }
            }
        }catch (Exception e) {
            log.error("Build child page failed! view path:[{}], {}",fxmlPath,e.toString());
            e.printStackTrace();
        }
        return view;
    }

    //=========================================================================

    public void setApp(App app) {
        this.app = app;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
        VideoDownController videoDownController = (VideoDownController) videoDownView.getController();
        videoDownController.setThreadPool(threadPool);
        M3u8Controller m3u8Controller = (M3u8Controller) m3u8View.getController();
        m3u8Controller.setThreadPool(threadPool);
        BiliDownController biliDownController = (BiliDownController) biliView.getController();
        biliDownController.setThreadPool(threadPool);
        SimpleModeController simpleModeController = (SimpleModeController) simpleModeView.getController();
        simpleModeController.setThreadPool(threadPool);
    }

}
