package pa.am.video_catcher.task;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.sapher.youtubedl.*;
import pa.am.video_catcher.bean.video.FormatInfo;
import pa.am.video_catcher.bean.video.FormatType;
import pa.am.video_catcher.bean.video.Quality;
import pa.am.video_catcher.bean.video.Setting;
import pa.am.video_catcher.controller.AbstractPageController;
import pa.am.video_catcher.ui.YDLDownloadListener;

import java.io.File;
import java.util.Map;

/**
 * youtube-dl的下载线程公共父类
 * @author Alan Min
 * @since 2021/2/22
 */
public abstract class AbstractYDLTask extends AbstractTask{

    protected final AbstractPageController controller;
    protected final Setting setting;

    public AbstractYDLTask(AbstractPageController controller, Setting setting) {
        this.controller = controller;
        this.setting = setting;
    }

    /**
     * 成功下载完后的结束工作
     */
    protected abstract void onFinishedWithSuccess(long startTime, YoutubeDLResponse response);

    /**
     * 下载失败后的结束工作
     * @param isFormatNotAvailable 失败是否因为请求的格式不支持
     */
    protected abstract void onFinishedWithError(long startTime, boolean isFormatNotAvailable);

    /**
     * 执行下载
     */
    protected void doDownload(Setting setting, long startTime) {
        YoutubeDLRequest request = new YoutubeDLRequest(setting.getUrl(), setting.getDownloadDir().getPath());
        request.setOption("ignore-errors");
        //组装参数
        boolean isFormat = packageOptions(setting,request);
        try {
            YDLDownloadListener downloadListener = new YDLDownloadListener(this,setting.getUrl().contains("youtube.com"),isFormat);
            YoutubeDLResponse response = YoutubeDL.execute(request,downloadListener);
            log.info("Download finished, exitCode[{}], elapsedTime[{}], Output info:\n{}",response.getExitCode(),response.getElapsedTime(),response.getOut());
            onFinishedWithSuccess(startTime,response);
        }catch (FormatNotAvailableException e0) {
            onFinishedWithError(startTime,true);
            log.warn("Requested format not available");
        } catch (YoutubeDLException e1) {
            onFinishedWithError(startTime,false);
            log.error("Got an error when download video: {}",e1.toString());
            e1.printStackTrace();
        }
    }

    /**
     * 组装参数
     * @return 是否设置了format参数，为true代表设置了
     */
    protected boolean packageOptions(Setting setting, YoutubeDLRequest request) {
        boolean isFormat = false;
        //下载文件的命名
        request.setOption("output", StringUtil.isNotNull(setting.getFileName()) ? setting.getFileName() : "%(id)s" );
        //格式组装
        StringBuilder format = new StringBuilder();
        FormatType formatType = setting.getFormatType();
        Quality quality = setting.getQuality();
        if(formatType==FormatType.AUDIO_ONLY) {
            if(quality==Quality.WORST) {
                format.append("worstaudio");
            }
            else if(quality==Quality.BEST) {
                format.append("bestaudio");
            }
        }
        else if(formatType==FormatType.VIDEO_ONLY) {
            if(quality==Quality.WORST) {
                format.append("worstvideo");
            }
            else if(quality==Quality.BEST) {
                format.append("bestvideo");
            }
        }
        else if(formatType==FormatType.FULL) {
            if(quality==Quality.WORST) {
                format.append("worst");
            }
        }

        if(setting.getFormatInfo()!=FormatInfo.ORIGINAL) {
            if(format.length()>0) {
                format.append("/");
            }
            format.append(setting.getFormatInfo().getSuffix());
        }
        if(setting.getQualityId()!=null) {
            if(format.length()>0) {
                format.append("/");
            }
            format.append(setting.getQualityId());
        }
        if(format.length()>0) {
            request.setOption("format",format.toString());
            isFormat = true;
        }
        //下载限速
        if(setting.getDownloadLimit()!=null) {
            request.setOption("limit-rate",setting.getDownloadLimit()+"M");
        }
        //ua
        if(StringUtil.isNotNull(setting.getUserAgent())) {
            request.setOption("user-agent",setting.getUserAgent());
        }
        //重试次数
        if(setting.getRetries()!=null) {
            request.setOption("retries",setting.getRetries());
        }
        //超时
        if(setting.getTimeOut()!=null) {
            request.setOption("socket-timeout",setting.getTimeOut());
        }
        //Cookie文件
        if(setting.getCookieFile()!=null) {
            request.setOption("cookies",setting.getCookieFile().getPath());
        }
        //格式转换
        if(setting.getTransFormat()!=null) {
            request.setOption("recode-video",setting.getTransFormat().getSuffix());
        }

        Map<String,String> options = request.getOption();
        for(Map.Entry<String,String> option : options.entrySet()) {
            log.info("YDL request option [{}] : [{}]",option.getKey(),option.getValue());
        }
        return isFormat;
    }//end packageOptions()

    /**
     * 重命名文件，修改后缀
     */
    protected void renameDownloadFile(YoutubeDLResponse response, Setting setting) {
        if(setting.getFormatInfo()!=FormatInfo.ORIGINAL) {
            File downloadFile = new File(setting.getDownloadDir().getPath() + File.separator + response.getYdlId());
            if(downloadFile.exists()) {
                File renameFile = new File(downloadFile.getPath() + "." + setting.getFormatInfo().getSuffix());
                if(!downloadFile.renameTo(renameFile)) {
                    log.error("Rename downloaded file failed, ydlId[{}]",response.getYdlId());
                }
            }
            else {
                log.error("Cant not rename downloaded file, file not exists, ydlId[{}], downloadFile path:{}",response.getYdlId(),downloadFile.getPath());
            }
        }
    }//end renameDownloadFile()

}
