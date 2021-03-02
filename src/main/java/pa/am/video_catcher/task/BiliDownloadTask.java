package pa.am.video_catcher.task;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import pa.am.scipioutils.common.StringUtil;
import pa.am.scipioutils.jfoenix.DialogHelper;
import pa.am.scipioutils.jfoenix.ProgressDialog;
import pa.am.video_catcher.bean.GlobalConst;
import pa.am.video_catcher.bean.video.BiliPage;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.catcher.bilibili.BilibiliCatcher;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;
import pa.am.video_catcher.controller.BiliDownController;
import pa.am.video_catcher.ui.BiliDownloadListener;

/**
 * @author Alan Min
 * @since 2021/3/2
 */
public class BiliDownloadTask extends AbstractTask{

    private final BiliDownController controller;
    private final Setting setting;
    private final BilibiliApi api;
    private final BiliPage page;
    private final ProgressDialog progressDialog;
    private final DownloadMode downloadMode;
    private final boolean isNewUrl;

    public BiliDownloadTask(BiliDownController controller, Setting setting, BilibiliApi api, BiliPage page, ProgressDialog progressDialog, DownloadMode downloadMode, boolean isNewUrl) {
        super(LogManager.getLogger(BiliDownloadTask.class));
        this.controller = controller;
        this.setting = setting;
        this.api = api;
        this.page = page;
        this.progressDialog = progressDialog;
        this.downloadMode = downloadMode;
        this.isNewUrl = isNewUrl;
    }

    @Override
    protected String call() {
        long startTime = System.currentTimeMillis();
        updateMessage("开始下载...");
        BilibiliCatcher catcher = buildCatcher();

        MediaPlay mediaPlay = page.getMediaPlay();
        //是否为新的url，如果是的话就获取一下数据，否则沿用之前的数据
        if(isNewUrl || mediaPlay==null) {
            try {
                BilibiliApi newApi = ( mediaPlay!=null ? catcher.getJsonFromApi(page.getVideoInfo(),page.getCid()) : catcher.getBilibiliApiFromHtml(setting.getUrl()) );
                api.setFromOtherApi(newApi);
                mediaPlay = newApi.getCurrentMediaPlay();
            }catch (Exception e) {
                log.error("Get bilibili data from html failed, url[{}], {}",setting.getUrl(),e.toString());
                e.printStackTrace();
                finishJob(startTime,false);
                return null;
            }
        }
        //设置进度显示
        BiliDownloadListener listener = new BiliDownloadListener(this);
        catcher.setDownloadListener(listener);
        //开始下载
        try {
            catcher.downloadMedia(setting.getUrl(),mediaPlay,setting.getQualityId(),downloadMode);
            finishJob(startTime,true);
        }catch (Exception e) {
            log.error("Download bilibili media failed, url[{}], {}",setting.getUrl(),e.toString());
            e.printStackTrace();
            finishJob(startTime,false);
        }
        return null;
    }

    private BilibiliCatcher buildCatcher() {
        BilibiliCatcher catcher = new BilibiliCatcher();
        catcher.setFileName(setting.getFileName());
        catcher.setDir(setting.getDownloadDir().getPath());
        catcher.setUserAgent(api.getUserAgent());
        if(setting.getRetries()!=null) {
            catcher.setRetryLimit(setting.getRetries());
        }
        if(StringUtil.isNotNull(api.getSessdata()) && StringUtil.isNotNull(api.getBili_jct())) {
            catcher.setLoginCookie(api.getSessdata(),api.getBili_jct());
        }
        if(StringUtil.isNotNull(api.getUserAgent())) {
            catcher.setUserAgent(api.getUserAgent());
        }
        return catcher;
    }

    private void finishJob(long startTime, final boolean isSuccess) {
        updateProgressInfo(1.0,"下载完成");
        keepThreadRunTime(startTime, GlobalConst.THREAD_KEEP_TIME);
        Platform.runLater(()->{
            controller.unbindTask2Progress(progressDialog);
            progressDialog.dismiss();
            if(isSuccess) {
                DialogHelper.showAlert(controller.getRootPane(),"下载完成","已成功下载!");
            }
            else {
                DialogHelper.showAlert(controller.getRootPane(),"下载失败","下载出现错误!");
            }
        });
    }

}
