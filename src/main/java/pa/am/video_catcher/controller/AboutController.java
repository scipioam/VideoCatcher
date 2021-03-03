package pa.am.video_catcher.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pa.am.scipioutils.jfoenix.fxml.BaseController;
import pa.am.video_catcher.bean.GlobalConst;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Alan Min
 * @since 2021/3/3
 */
public class AboutController extends BaseController {

    @FXML
    private ImageView imageView;
    @FXML
    private Label label_projectVersion;
    @FXML
    private Label label_ydlVersion;
    @FXML
    private Label label_ffmpegVersion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setImage(new Image("/img/developer_logo.png"));
    }

    public void setVersion(Map<String,String> versionMap) {
        label_projectVersion.setText("程序版本: "+versionMap.get(GlobalConst.VERSION_PROJECT));
        label_ydlVersion.setText("youtube-dl版本: "+versionMap.get(GlobalConst.VERSION_YOUTUBE_DL));
        label_ffmpegVersion.setText("ffmpeg版本: "+versionMap.get(GlobalConst.VERSION_FFMPEG));
    }

}
