package pa.am.video_catcher.controller;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_javafx.fxml.BaseController;
import com.github.ScipioAM.scipio_utils_javafx.util.FileChooseHelper;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.bean.video.TransFormat;
import pa.am.video_catcher.ui.NumericTextFieldOperator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 通用下载的高级设置界面controller
 * @author Alan Min
 * @since 2021/2/19
 */
public class AdvancedSettingController extends BaseController {

    private Stage thisStage;
    @FXML
    private StackPane rootPane;
    @FXML
    private Label label_cookieFile;
    @FXML
    private JFXTextField tf_reties;
    @FXML
    private JFXTextField tf_timeOut;
    @FXML
    private JFXTextField tf_userAgent;
    @FXML
    private JFXComboBox<TransFormat> cb_transFormat;

    private File cookieFile;
    private Setting setting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化输入框
        TextFormatter<String> textFormatter0 = new TextFormatter<>(new NumericTextFieldOperator());
        TextFormatter<String> textFormatter1 = new TextFormatter<>(new NumericTextFieldOperator());
        tf_reties.setTextFormatter(textFormatter0);
        tf_timeOut.setTextFormatter(textFormatter1);
        //初始化下拉框
        cb_transFormat.setConverter(new StringConverter<TransFormat>() {
            @Override
            public String toString(TransFormat object) {
                return object.getInfo();
            }
            @Override
            public TransFormat fromString(String string) {
                return null;
            }
        });
        cb_transFormat.getItems().addAll(TransFormat.NO_TRANS,TransFormat.MP4,TransFormat.MKV,TransFormat.FLV,TransFormat.AVI,TransFormat.WEBM,TransFormat.OGG);
        cb_transFormat.getSelectionModel().selectFirst();
    }

    //=========================================================================

    /**
     * 按钮：选择cookie文件
     */
    @FXML
    private void click_chooseCookie() {
        cookieFile = FileChooseHelper.chooseFile(rootPane.getScene().getWindow(),"选择Cookie文件");
        label_cookieFile.setText( cookieFile==null ? "" : cookieFile.getAbsolutePath() );
    }

    /**
     * 按钮：返回
     */
    @FXML
    private void click_return() {
        thisStage.hide();
    }

    /**
     * 按钮：设置生效
     */
    @FXML
    private void click_commit() {
        //重试次数
        String retriesStr = tf_reties.getText();
        if(StringUtil.isNotNull(retriesStr)) {
            int retries = Integer.parseInt(retriesStr);
            if(retries>0) {
                setting.setRetries(retries);
            }
            else {
                setting.setRetries(null);
                tf_reties.setText("");
            }
        }
        else {
            setting.setRetries(null);
        }
        //超时
        String timeOutStr = tf_timeOut.getText();
        if(StringUtil.isNotNull(timeOutStr)) {
            int timeOut = Integer.parseInt(timeOutStr);
            if(timeOut>0) {
                setting.setTimeOut(timeOut);
            }
            else {
                setting.setTimeOut(null);
                tf_timeOut.setText("");
            }
        }
        else {
            setting.setTimeOut(null);
        }
        //user-agent
        String ua = tf_userAgent.getText();
        setting.setUserAgent( StringUtil.isNull(ua) ? null : ua );
        //Cookie文件
        setting.setCookieFile(cookieFile);
        //格式转换
        TransFormat transFormat = cb_transFormat.getSelectionModel().getSelectedItem();
        setting.setTransFormat( transFormat==TransFormat.NO_TRANS ? null : transFormat );
    }

    //=========================================================================

    public void setThisStage(Stage thisStage) {
        this.thisStage = thisStage;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

}
