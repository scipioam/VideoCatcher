package pa.am.video_catcher.controller;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pa.am.video_catcher.bean.video.*;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.task.SimpleBiliDownTask;
import pa.am.video_catcher.task.SimpleM3u8Task;
import pa.am.video_catcher.task.SimpleVideoDownTask;
import pa.am.video_catcher.util.DesktopUtil;

import java.net.URL;
import java.util.Objects;
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
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/simple_mode_logo.png"))));
    }

    //=========================================================================

    /**
     * 按钮：下载
     */
    public void click_download() {
        String url = tf_url.getText();
        if(StringUtil.isNull(url)) {
            return;
        }
        progressBar.setVisible(true);
        //根据url启动不同的下载器
        if(url.contains(".m3u8")) {
            m3u8Download(url);
        }
        else if(url.contains("bilibili.com")) {
            bilibiliDownload(url);
        }
        else {
            commonDownload(url);
        }
    }

    /**
     * m3u8下载
     */
    private void m3u8Download(String url) {
        SimpleM3u8Task task = new SimpleM3u8Task(url,DesktopUtil.getDesktopFile(),System.currentTimeMillis()+"",this,progressBar,label_progress);
        bindTask2Progress(task,label_progress,progressBar);
        threadPool.submit(task);
    }

    /**
     * b站视频下载
     */
    private void bilibiliDownload(String url) {
        Setting setting = new Setting();
        setting.setUrl(url);
        setting.setDownloadDir(DesktopUtil.getDesktopFile());

        SimpleBiliDownTask task = new SimpleBiliDownTask(setting,new BilibiliApi(), new BiliPage(), DownloadMode.FULL,true,this,progressBar,label_progress);
        bindTask2Progress(task,label_progress,progressBar);
        threadPool.submit(task);
    }

    /**
     * 通用视频下载（youtube-dl下载）
     */
    private void commonDownload(String url) {
        Setting setting = new Setting();
        setting.setUrl(url);
        setting.setDownloadDir(DesktopUtil.getDesktopFile());//输出到桌面
        setting.setFormatType(FormatType.FULL);
        setting.setQuality(Quality.DEFAULT);
        setting.setFormatInfo(FormatInfo.ORIGINAL);

        SimpleVideoDownTask task = new SimpleVideoDownTask(this,setting,progressBar,label_progress);
        bindTask2Progress(task,label_progress,progressBar);
        threadPool.submit(task);
    }

}
