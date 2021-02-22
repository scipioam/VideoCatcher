package pa.am.video_catcher.ui;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import pa.am.video_catcher.bean.video.FormatType;
import pa.am.video_catcher.bean.video.FormatInfo;

/**
 * @author Alan Min
 * @since 2021/2/20
 */
public class DownloadFormatChangeListener implements ChangeListener<FormatType> {

    private final JFXComboBox<FormatInfo> cb_formatInfo;

    public DownloadFormatChangeListener(JFXComboBox<FormatInfo> cb_formatInfo) {
        this.cb_formatInfo = cb_formatInfo;
    }

    @Override
    public void changed(ObservableValue<? extends FormatType> observable, FormatType oldValue, FormatType newValue) {
        ObservableList<FormatInfo> formatInfoList = cb_formatInfo.getItems();
        if(newValue== FormatType.AUDIO_ONLY) {
            formatInfoList.clear();
            formatInfoList.addAll(FormatInfo.ORIGINAL, FormatInfo.MP3, FormatInfo.M4A, FormatInfo.FLAC, FormatInfo.AAC, FormatInfo.WAV);
            cb_formatInfo.getSelectionModel().selectFirst();
        }
        else {
            formatInfoList.clear();
            formatInfoList.addAll(FormatInfo.ORIGINAL, FormatInfo.MP4, FormatInfo.FLV, FormatInfo.WEBM, FormatInfo.THIRD_GP);
            cb_formatInfo.getSelectionModel().selectFirst();
        }
    }//end changed()

}
