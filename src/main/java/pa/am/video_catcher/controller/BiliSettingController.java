package pa.am.video_catcher.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.jfoenix.fxml.BaseController;
import pa.am.scipioutils.jfoenix.snackbar.JFXSnackbarHelper;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * B站登录设置controller
 * @author Alan Min
 * @since 2021/3/2
 */
public class BiliSettingController extends BaseController {

    @FXML
    private JFXTextField tf_sessdata;
    @FXML
    private JFXTextField tf_bili_jct;
    @FXML
    private JFXTextField tf_userAgent;

    private Stage thisStage;
    private BilibiliApi api;
    private StackPane parentRootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    //=========================================================================

    /**
     * 按钮：确定
     */
    @FXML
    private void click_commit() {
        String sessdata = tf_sessdata.getText();
        String bili_jct = tf_bili_jct.getText();
        String userAgent = tf_userAgent.getText();
        boolean isSetData = false;
        if(StringUtil.isNotNull(sessdata) && StringUtil.isNotNull(bili_jct)) {
            api.setSessdata(sessdata);
            api.setBili_jct(bili_jct);
            isSetData = true;
        }
        if(StringUtil.isNotNull(userAgent)) {
            api.setUserAgent(userAgent);
            isSetData = true;
        }
        thisStage.hide();
        if(isSetData) {
            JFXSnackbarHelper.showSuccess(parentRootPane,"设置成功");
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

    public void setApi(BilibiliApi api) {
        this.api = api;
    }

    public void setParentRootPane(StackPane parentRootPane) {
        this.parentRootPane = parentRootPane;
    }
}
