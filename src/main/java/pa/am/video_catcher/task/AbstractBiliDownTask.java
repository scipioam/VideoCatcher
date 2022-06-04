package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import pa.am.video_catcher.bean.video.BiliPage;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.catcher.bilibili.BilibiliCatcher;
import pa.am.video_catcher.catcher.bilibili.bean.BilibiliApi;
import pa.am.video_catcher.catcher.bilibili.bean.DownloadMode;
import pa.am.video_catcher.catcher.bilibili.bean.media_play.MediaPlay;
import pa.am.video_catcher.ui.BiliDownloadListener;

/**
 * @author Alan Min
 * @since 2021/3/3
 */
public abstract class AbstractBiliDownTask extends AbstractTask{

    protected final Setting setting;
    protected final BilibiliApi api;
    protected final BiliPage page;
    protected final DownloadMode downloadMode;
    protected final boolean isNewUrl;

    public AbstractBiliDownTask(Setting setting, BilibiliApi api, BiliPage page, DownloadMode downloadMode, boolean isNewUrl) {
        this.setting = setting;
        this.api = api;
        this.page = page;
        this.downloadMode = downloadMode;
        this.isNewUrl = isNewUrl;
    }

    protected abstract void finishJob(long startTime, final boolean isSuccess);

    protected void doDownload(long startTime) {
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
                return;
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

}
