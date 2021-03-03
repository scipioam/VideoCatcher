package pa.am.video_catcher.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;
import pa.am.scipioutils.common.DateTimeUtil;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.jfoenix.DialogHelper;
import pa.am.scipioutils.jfoenix.ProgressDialog;
import pa.am.scipioutils.jfoenix.fxml.FxmlView;
import pa.am.scipioutils.jfoenix.snackbar.JFXSnackbarHelper;
import pa.am.video_catcher.bean.ui.ColumnType;
import pa.am.video_catcher.bean.ui.FormatVO;
import pa.am.video_catcher.bean.video.BiliPage;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.catcher.bilibili.bean.video_info.*;
import pa.am.video_catcher.task.BiliCoverDownloadTask;
import pa.am.video_catcher.task.BiliDownloadTask;
import pa.am.video_catcher.task.GetBiliVideoInfoTask;
import pa.am.video_catcher.ui.BiliPageChangeListener;
import pa.am.video_catcher.ui.NumericTextFieldOperator;
import pa.am.video_catcher.util.TableViewInit;

import java.net.URL;
import java.util.*;

/**
 * b站视频下载界面
 * @author Alan Min
 * @since 2021/2/21
 */
public class BiliDownController extends AbstractPageController{

    @FXML
    private Label label_path;
    @FXML
    private JFXTextField tf_url;
    @FXML
    private JFXTextField tf_rename;
    @FXML
    private JFXTextField tf_retryLimit;
    @FXML
    private Label label_uploader;//上传者
    @FXML
    private Label label_title;//视频标题
    @FXML
    private Label label_uploadDate;//上传日期
    @FXML
    private Label label_bvId;//视频BV号
    @FXML
    private Label label_view;//视频播放量
    @FXML
    private Label label_like;//点赞数
    @FXML
    private Label label_coin;//硬币数
    @FXML
    private Label label_favorite;//收藏数
    @FXML
    private JFXComboBox<BiliPage> cb_page;
    @FXML
    private JFXToggleButton toggleBtn_downloadMode;//是否下载音频+视频
    @FXML
    private JFXTreeTableView<FormatVO> tableView;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_formatId;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_note;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_codec;
    @FXML
    private JFXTreeTableColumn<FormatVO,String> tc_resolution;

    private FxmlView biliSettingView;

    private BiliPageChangeListener biliPageChangeListener;
    //当前画面显示的格式列表
    private final ObservableList<FormatVO> currentFormatList = FXCollections.observableArrayList();
    //所有分p的格式列表，key是cid
    private final Map<Long, List<FormatVO>> formatListMap = new HashMap<>();
    //获取的b站下载信息（上一次）
    private final BilibiliApi lastApi = new BilibiliApi();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTextFields();
        initComboBox();
        initTableView();
    }

    private void initTextFields() {
        TextFormatter<String> textFormatter0 = new TextFormatter<>(new NumericTextFieldOperator());
        tf_retryLimit.setTextFormatter(textFormatter0);
    }

    private void initComboBox() {
        cb_page.setConverter(new StringConverter<BiliPage>() {
            @Override
            public String toString(BiliPage object) {
                return object.getTitle();
            }
            @Override
            public BiliPage fromString(String string) {
                return null;
            }
        });
        biliPageChangeListener = new BiliPageChangeListener(tableView,formatListMap,currentFormatList);
        cb_page.getSelectionModel().selectedItemProperty().addListener(biliPageChangeListener);
        cb_page.getItems().add(new BiliPage("未获取信息"));
        cb_page.getSelectionModel().selectFirst();
    }

    private void initTableView() {
        TableViewInit.initStrColumn(tc_formatId, ColumnType.FORMAT_ID);
        TableViewInit.initStrColumn(tc_note, ColumnType.NOTE);
        TableViewInit.initStrColumn(tc_codec, ColumnType.CODEC);
        TableViewInit.initStrColumn(tc_resolution, ColumnType.RESOLUTION);
        final TreeItem<FormatVO> root = new RecursiveTreeItem<>(currentFormatList, RecursiveTreeObject::getChildren);
        tableView.setRoot(root);
        tableView.setShowRoot(false);
    }

    @Override
    public void onStop() {
        cb_page.getSelectionModel().selectedItemProperty().removeListener(biliPageChangeListener);
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
        else if(!url.contains("bilibili.com")) {
            JFXSnackbarHelper.showWarn(rootPane, "输入的url不是B站的");
            return;
        }

        ProgressDialog progressDialog = DialogHelper.prepareProgress(rootPane,"获取中...");
        progressDialog.show();
        //第一次（或新的一次）获取视频信息
        if(!url.equals(lastApi.getRequestUrl())) {
            GetBiliVideoInfoTask task = new GetBiliVideoInfoTask(url,this,progressDialog,formatListMap,true,null,null,lastApi.getSessdata(),lastApi.getBili_jct(),lastApi.getUserAgent());
            threadPool.submit(task);
        }
        //已经获取过，重新获取（可能是另外p的format信息）
        else {
            BiliPage page = cb_page.getSelectionModel().getSelectedItem();
            GetBiliVideoInfoTask task = new GetBiliVideoInfoTask(url,this,progressDialog,formatListMap,false,page.getVideoInfo(),page.getCid(),lastApi.getSessdata(),lastApi.getBili_jct(),lastApi.getUserAgent());
            threadPool.submit(task);
        }
        lastApi.setRequestUrl(url);
    }

    /**
     * 按钮：下载封面
     */
    @FXML
    private void click_downloadCover() {
        Setting setting = buildSetting();
        if(setting==null) {
            return;
        }
        String coverUrl = ( setting.getUrl().equals(lastApi.getRequestUrl()) ? lastApi.getCoverUrl() : null );
        ProgressDialog progressDialog = DialogHelper.prepareProgress(rootPane,"下载中...");
        progressDialog.show();
        BiliCoverDownloadTask task = new BiliCoverDownloadTask(this,setting,coverUrl,progressDialog);
        bindTask2Progress(task,progressDialog);
        threadPool.submit(task);
    }

    /**
     * 按钮：下载音视频
     * TODO 待进一步测试
     */
    @FXML
    private void click_downloadMedia() {
        Setting setting = buildSetting();
        if(setting==null) {
            return;
        }
        //组装下载模式及下载格式的id
        DownloadMode downloadMode;
        Integer qualityId = null;
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if(selectedIndex>=0) {
            FormatVO model = currentFormatList.get(selectedIndex);
            if(model.getNote().equals("音频")) {
                downloadMode = DownloadMode.AUDIO_ONLY;
            }
            else {
                downloadMode = ( toggleBtn_downloadMode.isSelected() ? DownloadMode.FULL : DownloadMode.VIDEO_ONLY );
            }
            qualityId = Integer.parseInt(model.getFormatId());
        }
        else {
            downloadMode = ( toggleBtn_downloadMode.isSelected() ? DownloadMode.FULL : DownloadMode.VIDEO_ONLY );
        }
        setting.setQualityId(qualityId);
        //确定是否为新的url
        boolean isNewUrl = ( !setting.getUrl().equals(lastApi.getRequestUrl()) );
        BiliPage page = cb_page.getSelectionModel().getSelectedItem();
        //开始下载
        ProgressDialog progressDialog = DialogHelper.prepareProgress(rootPane,"开始下载");
        progressDialog.show();
        BiliDownloadTask task = new BiliDownloadTask(this,setting,lastApi,page,progressDialog,downloadMode,isNewUrl);
        bindTask2Progress(task,progressDialog);
        threadPool.submit(task);
    }

    /**
     * 按钮：登录设置
     */
    @FXML
    private void click_setCookie() {
        if(biliSettingView==null) {
            biliSettingView = FxmlView.showView(biliSettingView,"/view/bili_setting.fxml",
                    "登录设置", rootPane.getScene().getWindow());
            BiliSettingController controller = (BiliSettingController) biliSettingView.getController();
            controller.setThisStage(biliSettingView.getStage());
            controller.setApi(lastApi);
            controller.setParentRootPane(rootPane);
        }
        else{
            biliSettingView.getStage().show();
        }
    }

    //=========================================================================

    /**
     * 更新视频信息
     * @param newApi 信息对象
     */
    public void updateVideoInfo(BilibiliApi newApi, ProgressDialog progressDialog, boolean isFirst, Long cid) {
        if(!isFirst) {
            List<FormatVO> newFormatList = formatListMap.get(cid);
            currentFormatList.clear();
            currentFormatList.addAll(newFormatList);
            tableView.refresh();
            progressDialog.dismiss();
            return;
        }

        cb_page.getItems().clear();
        //获取成功
        if(newApi!=null) {
            VideoInfo videoInfo = newApi.getVideoInfo();
            if(videoInfo==null) {
                updateVideoInfoFailed();
                return;
            }
            lastApi.setFromOtherApi(newApi);
            VideoData data = videoInfo.getVideoData();
            Owner up = data.getOwner();
            Stat stat = data.getStat();
            label_bvId.setText(videoInfo.getBvid());
            label_uploader.setText(up.getName());
            label_uploadDate.setText(DateTimeUtil.getObjFromTimestamp(data.getPubdate()*1000L).toString());
            label_title.setText(data.getTitle());
            label_view.setText(stat.getView()+"");
            label_like.setText(stat.getLike()+"");
            label_coin.setText(stat.getCoin()+"");
            label_favorite.setText(stat.getFavorite()+"");
            List<Page> pageList = videoInfo.getPages();
            for(Page page : pageList) {
                BiliPage uiPage = new BiliPage(page.getPart(),page.getCid(),videoInfo.getAid());
                uiPage.setVideoInfo(videoInfo);
                if(page.getPage()==1) {
                    uiPage.setMediaPlay(newApi.getCurrentMediaPlay());
                }
                cb_page.getItems().add(uiPage);
            }//end for
        }//end outside if
        //获取失败
        else {
            updateVideoInfoFailed();
        }
        cb_page.getSelectionModel().selectFirst();
        tableView.refresh();
        progressDialog.dismiss();
    }

    private void updateVideoInfoFailed() {
        String s = "获取失败";
        label_bvId.setText(s);
        label_uploader.setText(s);
        label_uploadDate.setText(s);
        label_title.setText(s);
        label_view.setText(s);
        label_like.setText(s);
        label_coin.setText(s);
        label_favorite.setText(s);
        currentFormatList.clear();
        cb_page.getItems().add(new BiliPage("未获取信息"));
    }

    public BilibiliApi getLastApi() {
        return lastApi;
    }

    private Setting buildSetting() {
        String videoUrl = tf_url.getText();
        if(isInvalidInputs(videoUrl,downloadDir)) {
            return null;
        }
        Setting setting = new Setting();
        setting.setDownloadDir(downloadDir);
        setting.setUrl(videoUrl);
        setting.setFileName(tf_rename.getText());
        String retryLimitStr = tf_retryLimit.getText();
        if(StringUtil.isNotNull(retryLimitStr)) {
            int retryLimit = Integer.parseInt(retryLimitStr);
            if(retryLimit>0) {
                setting.setRetries(retryLimit);
            }
        }
        return setting;
    }
}
