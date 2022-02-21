package pa.am.video_catcher.controller;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_javafx.snackbar.JFXSnackbarHelper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import pa.am.video_catcher.task.M3u8DownloadTask;
import pa.am.video_catcher.ui.NumericTextFieldOperator;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * m3u8下载的子界面controller
 * @author Alan Min
 * @since 2021/2/18
 */
public class M3u8Controller extends AbstractPageController {

    @FXML
    private JFXButton btn_download;
    @FXML
    private JFXButton btn_chooseDir;
    @FXML
    private Label label_path;//已选择目录
    @FXML
    private JFXTextField tf_url;//url
    @FXML
    private JFXTextField tf_rename;//下载文件重命名
    @FXML
    private JFXTextField tf_threadLimit;//下载线程数
    @FXML
    private JFXTextField tf_retryLimit;//重试次数
    @FXML
    private JFXTextField tf_fileSuffix;//下载文件后缀
    @FXML
    private Label label_progress;//下载进度文本
    @FXML
    private JFXProgressBar progressBar;//下载进度条

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setVisible(false);
        initTextFields();
    }

    private void initTextFields() {
        TextFormatter<String> textFormatter0 = new TextFormatter<>(new NumericTextFieldOperator(3));
        TextFormatter<String> textFormatter1 = new TextFormatter<>(new NumericTextFieldOperator(2));
        tf_threadLimit.setTextFormatter(textFormatter0);
        tf_retryLimit.setTextFormatter(textFormatter1);
    }

    //=========================================================================

    /**
     * 按钮：选择下载目录
     */
    @FXML
    private void click_chooseDir() {
        chooseDir(label_path);
    }

    /**
     * 按钮：下载
     */
    @FXML
    private void click_download() {
        //必填项检查
        String url = tf_url.getText();
        String fileName = tf_rename.getText();
        if(isInvalidInputs(url,downloadDir)) {
            return;
        }
        if(StringUtil.isNull(fileName)) {
            JFXSnackbarHelper.showWarn(rootPane,"请输入下载文件名!");
            return;
        }
        //选填项检查
        Integer threadLimit = null;
        Integer retryLimit = null;
        String threadLimitStr = tf_threadLimit.getText();
        String retryLimitStr = tf_retryLimit.getText();
        String fileSuffix = tf_fileSuffix.getText();
        if(StringUtil.isNotNull(threadLimitStr)) {
            threadLimit = Integer.parseInt(threadLimitStr);
        }
        if(StringUtil.isNotNull(retryLimitStr)) {
            retryLimit = Integer.parseInt(retryLimitStr);
        }

//        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressBar.setVisible(true);
        setBtnDisable(true);

        M3u8DownloadTask downloadTask = new M3u8DownloadTask(this,url,downloadDir,fileName,threadLimit,retryLimit,fileSuffix);
        bindTask2Progress(downloadTask,label_progress,progressBar);//界面控件与下载线程绑定
        //启动子线程
        threadPool.submit(downloadTask);
    }

    //=========================================================================

    public void unbindTask2Progress() {
        super.unbindTask2Progress(label_progress, progressBar);
    }

    public void setBtnDisable(boolean disable) {
        btn_download.setDisable(disable);
        btn_chooseDir.setDisable(disable);
    }

}
