package pa.am.video_catcher.controller;

import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pa.am.scipioutils.common.PropertiesHelper;
import pa.am.scipioutils.jfoenix.DialogHelper;
import pa.am.scipioutils.jfoenix.fxml.BaseController;
import pa.am.video_catcher.App;
import pa.am.video_catcher.bean.GlobalConst;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 设置启动界面的controller
 * @author Alan Min
 * @since 2021/2/22
 */
public class LaunchSettingController extends BaseController {

    private final Logger log = LogManager.getLogger(LaunchSettingController.class);
    private Stage thisStage;
    private StackPane mainRootPane;
    private App app;

    private final ToggleGroup toggleGroup = new ToggleGroup();
    @FXML
    private JFXRadioButton rBtn_launchSimple;//以极简模式启动
    @FXML
    private JFXRadioButton rBtn_launchPro;//以高级模式启动

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rBtn_launchSimple.setId(GlobalConst.CONFIG_LAUNCH_MODE_SIMPLE);
        rBtn_launchPro.setId(GlobalConst.CONFIG_LAUNCH_MODE_PRO);
        rBtn_launchSimple.setToggleGroup(toggleGroup);
        rBtn_launchPro.setToggleGroup(toggleGroup);
    }

    //=========================================================================

    /**
     * 按钮：确定
     */
    @FXML
    private void click_commit() {
        JFXRadioButton rBtn = (JFXRadioButton) toggleGroup.getSelectedToggle();
        if(rBtn==null) {
            return;
        }
        else if(app.getSavedLaunchMode().equals(rBtn.getId())) {
            thisStage.hide();
            DialogHelper.showAlert(mainRootPane,"设置结果","本来就是以这个模式启动\n您改了个寂寞...");
            return;
        }
        PropertiesHelper helper = new PropertiesHelper(GlobalConst.CONFIG_FILE_PATH);
        Map<String,String> config = new HashMap<>();
        config.put(GlobalConst.CONFIG_LAUNCH_MODE,rBtn.getId());
        try {
            helper.saveConfig(config,null);
            log.info("Modify config file success, new launch mode: {}",rBtn.getId());
            thisStage.hide();
            DialogHelper.showAlert(mainRootPane,"设置成功","已成功修改启动界面");
        }catch (Exception e) {
            log.error("Save launch mode config failed! {}",e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 按钮：返回
     */
    @FXML
    private void click_return() {
        thisStage.hide();
    }

    //=========================================================================

    public void setThisStage(Stage thisStage) {
        this.thisStage = thisStage;
    }

    public void setMainRootPane(StackPane mainRootPane) {
        this.mainRootPane = mainRootPane;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
