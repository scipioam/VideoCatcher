package pa.am.video_catcher.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sapher.youtubedl.mapper.VideoFormat;
import com.sapher.youtubedl.mapper.VideoInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.jfoenix.DialogHelper;
import pa.am.scipioutils.jfoenix.ProgressDialog;
import pa.am.scipioutils.jfoenix.fxml.FxmlView;
import pa.am.scipioutils.jfoenix.snackbar.JFXSnackbarHelper;
import pa.am.video_catcher.bean.ui.*;
import pa.am.video_catcher.bean.video.FormatInfo;
import pa.am.video_catcher.bean.video.FormatType;
import pa.am.video_catcher.bean.video.Quality;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.ui.DownloadFormatChangeListener;
import pa.am.video_catcher.task.GetVideoInfoTask;
import pa.am.video_catcher.task.VideoDownloadTask;
import pa.am.video_catcher.ui.NumericTextFieldOperator;
import pa.am.video_catcher.util.TableViewInit;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 视频下载(youtube-dl)的子界面controller
 * @author Alan Min
 * @since 2021/2/18
 */
public class VideoDownController extends AbstractPageController {

//    private final Logger log = LogManager.getLogger(VideoDownController.class);

    @FXML
    private JFXTextField tf_url;
    @FXML
    private Label label_path;//已选择目录
    @FXML
    private JFXTextField tf_rename;//下载文件重命名
    @FXML
    private JFXTextField tf_downloadRateLimit;//下载限速
    @FXML
    private JFXComboBox<FormatType> cb_formatType;
    @FXML
    private JFXComboBox<Quality> cb_quality;
    @FXML
    private JFXComboBox<FormatInfo> cb_formatInfo;
    @FXML
    private Label label_uploader;//上传者
    @FXML
    private Label label_title;//视频标题
    @FXML
    private Label label_uploadDate;//上传日期
    @FXML
    private Label label_id;//视频ID
    @FXML
    private Label label_size;//视频尺寸

    @FXML
    private JFXTreeTableView<FormatVO> tableView;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_formatId;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_note;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_fps;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_extension;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_fileSize;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_resolution;

    private final Setting setting = new Setting();
    private FxmlView advancedSettingView;//高级设置view
    private DownloadFormatChangeListener dfChangeListener;
    private final ObservableList<FormatVO> formatVOList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTextFields();
        initComboBox();
        initTableView();
    }

    private void initTextFields() {
        TextFormatter<String> textFormatter0 = new TextFormatter<>(new NumericTextFieldOperator());
        tf_downloadRateLimit.setTextFormatter(textFormatter0);
    }

    private void initComboBox() {
        cb_formatType.setConverter(new StringConverter<FormatType>() {
            @Override
            public String toString(FormatType object) {
                return object.getName();
            }
            @Override
            public FormatType fromString(String string) {
                return null;
            }
        });
        cb_quality.setConverter(new StringConverter<Quality>() {
            @Override
            public String toString(Quality object) {
                return object.getName();
            }
            @Override
            public Quality fromString(String string) {
                return null;
            }
        });
        cb_formatInfo.setConverter(new StringConverter<FormatInfo>() {
            @Override
            public String toString(FormatInfo object) {
                return object.getSuffix();
            }
            @Override
            public FormatInfo fromString(String string) {
                return null;
            }
        });
        cb_formatType.getItems().addAll(FormatType.FULL, FormatType.VIDEO_ONLY, FormatType.AUDIO_ONLY);
        dfChangeListener = new DownloadFormatChangeListener(cb_formatInfo);
        cb_formatType.getSelectionModel().selectedItemProperty().addListener(dfChangeListener);
        cb_formatType.getSelectionModel().selectFirst();
        cb_quality.getItems().addAll(Quality.DEFAULT,Quality.BEST,Quality.WORST);
        cb_quality.getSelectionModel().selectFirst();
    }

    private void initTableView() {
        TableViewInit.initStrColumn(tc_formatId, ColumnType.FORMAT_ID);
        TableViewInit.initStrColumn(tc_note, ColumnType.NOTE);
        TableViewInit.initStrColumn(tc_fps, ColumnType.FPS);
        TableViewInit.initStrColumn(tc_extension, ColumnType.EXTENSION);
        TableViewInit.initStrColumn(tc_fileSize, ColumnType.FILE_SIZE);
        TableViewInit.initStrColumn(tc_resolution, ColumnType.RESOLUTION);
        final TreeItem<FormatVO> root = new RecursiveTreeItem<>(formatVOList, RecursiveTreeObject::getChildren);
        tableView.setRoot(root);
        tableView.setShowRoot(false);
    }

    @Override
    public void onStop() {
        cb_formatType.getSelectionModel().selectedItemProperty().removeListener(dfChangeListener);
    }

    //=========================================================================

    /**
     * 按钮：选择下载目录-m3u8
     */
    @FXML
    private void click_chooseDir() {
        chooseDir(label_path);
    }

    /**
     * 按钮：获取信息
     */
    @FXML
    private void click_getInfo() {
        String url = tf_url.getText();
        if(StringUtil.isNull(url)) {
            JFXSnackbarHelper.showWarn(rootPane, "请输入视频url");
            return;
        }
        else if(!StringUtil.isHttpUrl(url)) {
            JFXSnackbarHelper.showWarn(rootPane, "请输入合法的url");
            return;
        }
        ProgressDialog progressDialog = DialogHelper.prepareProgress(rootPane,"获取中...");
        progressDialog.show();
        GetVideoInfoTask task = new GetVideoInfoTask(url,this,progressDialog);
        threadPool.submit(task);
    }

    /**
     * 按钮：下载（基于youtube-dl）
     */
    @FXML
    private void click_download() {
        String url = tf_url.getText();
        if(isInvalidInputs(url,downloadDir)) {
            return;
        }

        FormatType formatType = cb_formatType.getSelectionModel().getSelectedItem();
        Integer selectedFormatId;
        int selectedFormatIndex = tableView.getSelectionModel().getSelectedIndex();
        if(selectedFormatIndex>=0) {
            FormatVO selectedFormat = formatVOList.get(selectedFormatIndex);
            selectedFormatId = Integer.parseInt(selectedFormat.getFormatId());
        }
        else {
            selectedFormatId = null;
        }

        if(formatType==FormatType.VIDEO_ONLY) {
            DialogHelper.showAlert(rootPane,"注   意","原视频可能不支持单独下载视频\n(而缺少音频)\n点击确定继续下载",
                    (actionEvent, jfxDialog) -> doDownload(url,formatType,selectedFormatId));
        }
        else if(formatType==FormatType.AUDIO_ONLY) {
            DialogHelper.showAlert(rootPane,"注   意","原视频可能不支持单独下载音频\n点击确定继续下载",
                    (actionEvent, jfxDialog) -> doDownload(url,formatType,selectedFormatId));
        }
        else {
            doDownload(url,formatType,selectedFormatId);
        }
    }//end click_download()

    /**
     * 执行下载工作
     */
    private void doDownload(String url, FormatType formatType, Integer selectedFormatId) {
        String fileName = tf_rename.getText();
        Quality quality = cb_quality.getSelectionModel().getSelectedItem();
        FormatInfo formatInfo = cb_formatInfo.getSelectionModel().getSelectedItem();
        String downloadLimitStr = tf_downloadRateLimit.getText();
        if(StringUtil.isNotNull(downloadLimitStr)) {
            setting.setDownloadLimit(Integer.valueOf(downloadLimitStr));
        }
        setting.setDownloadDir(downloadDir);
        setting.setFileName(fileName);
        setting.setUrl(url);
        setting.setFormatType(formatType);
        setting.setQuality(quality);
        setting.setFormatInfo(formatInfo);
        setting.setQualityId(selectedFormatId);

        ProgressDialog progressDialog = DialogHelper.prepareProgress(rootPane,"开始下载...");
        progressDialog.show();
        VideoDownloadTask task = new VideoDownloadTask(this,setting,progressDialog);
        bindTask2Progress(task,progressDialog);
        threadPool.submit(task);
    }

    /**
     * 按钮：高级选项
     */
    @FXML
    private void click_advancedSetting() {
        if(advancedSettingView==null) {
            advancedSettingView = FxmlView.showView(advancedSettingView,"/view/advanced_setting.fxml",
                    "高级下载设置", rootPane.getScene().getWindow());
            AdvancedSettingController asController = (AdvancedSettingController) advancedSettingView.getController();
            asController.setThisStage(advancedSettingView.getStage());
            asController.setSetting(setting);
        }
        else{
            advancedSettingView.getStage().show();
        }
    }//end click_advancedSetting()

    //=========================================================================

    /**
     * 更新视频信息
     * @param info 信息对象
     */
    public void updateVideoInfo(boolean isYoutubeUrl, VideoInfo info, ProgressDialog progressDialog) {
        //获取成功
        if(info!=null) {
            label_title.setText(info.title);
            label_uploader.setText(info.uploader);
            label_uploadDate.setText(info.uploadDate);
            label_id.setText(info.id);
            label_size.setText(info.width + " x " + info.height);
            formatVOList.clear();
            List<VideoFormat> formatList = info.formats;
            for(VideoFormat data : formatList) {
                FormatVO vo = FormatVO.build(isYoutubeUrl,data);
                formatVOList.add(vo);
            }
        }
        //获取失败
        else {
            label_title.setText("获取失败");
            label_uploader.setText("获取失败");
            label_uploadDate.setText("获取失败");
            label_id.setText("获取失败");
            label_size.setText("获取失败");
            formatVOList.clear();
        }
        tableView.refresh();
        progressDialog.dismiss();
    }//end updateVideoInfo()

}
