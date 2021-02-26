package pa.am.video_catcher.controller;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pa.am.scipioutils.common.StringUtil;
import pa.am.video_catcher.bean.video.FormatInfo;
import pa.am.video_catcher.bean.video.FormatType;
import pa.am.video_catcher.bean.video.Quality;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.task.SimpleVideoDownTask;
import pa.am.video_catcher.util.DesktopUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 极简模式controller
 * @author Alan Min
 * @since 2021/2/22
 */
public class SimpleModeController extends AbstractPageController{

    @FXML
    private ImageView imageView;
    @FXML
    private JFXTextField tf_url;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label label_progress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setVisible(false);
        imageView.setImage(new Image(getClass().getResourceAsStream("/img/simple_mode_logo.png")));
    }

    //=========================================================================

    //TODO 要根据url启动不同的下载器
    /**
     * 按钮：下载
     */
    public void click_download() {
        String url = tf_url.getText();
        if(StringUtil.isNull(url)) {
            return;
        }
        Setting setting = new Setting();
        setting.setUrl(url);
        setting.setDownloadDir(DesktopUtil.getDesktopFile());//输出到桌面
        setting.setFormatType(FormatType.FULL);
        setting.setQuality(Quality.DEFAULT);
        setting.setFormatInfo(FormatInfo.ORIGINAL);

        progressBar.setVisible(true);
        SimpleVideoDownTask task = new SimpleVideoDownTask(this,setting,progressBar,label_progress);
        bindTask2Progress(task,label_progress,progressBar);
        threadPool.submit(task);
    }

}
