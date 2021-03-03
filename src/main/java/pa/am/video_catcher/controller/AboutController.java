package pa.am.video_catcher.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pa.am.scipioutils.jfoenix.fxml.BaseController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Alan Min
 * @since 2021/3/3
 */
public class AboutController extends BaseController {

    @FXML
    private ImageView imageView;
    @FXML
    private Label label_version;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setImage(new Image("/img/developer_logo.png"));
    }

    public void setVersion(String version) {
        label_version.setText("版本: "+version);
    }

}
