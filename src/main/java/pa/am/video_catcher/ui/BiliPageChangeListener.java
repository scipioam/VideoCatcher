package pa.am.video_catcher.ui;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import pa.am.video_catcher.bean.ui.FormatModel;
import pa.am.video_catcher.bean.video.BiliPage;

import java.util.List;
import java.util.Map;

/**
 * B站分p下载的下拉框变化监听器
 * @author Alan Min
 * @since 2021/2/26
 */
public class BiliPageChangeListener implements ChangeListener<BiliPage> {

    private final JFXTreeTableView<FormatModel> tableView;
    private final Map<Long, List<FormatModel>> formatListMap;
    private final ObservableList<FormatModel> currentFormatList;

    public BiliPageChangeListener(JFXTreeTableView<FormatModel> tableView, Map<Long, List<FormatModel>> formatListMap, ObservableList<FormatModel> currentFormatList) {
        this.tableView = tableView;
        this.formatListMap = formatListMap;
        this.currentFormatList = currentFormatList;
    }

    @Override
    public void changed(ObservableValue<? extends BiliPage> observable, BiliPage oldValue, BiliPage newValue) {
        if(newValue==null) {
            return;
        }
        List<FormatModel> formatList = formatListMap.get(newValue.getCid());
        currentFormatList.clear();
        if(formatList!=null) {
            currentFormatList.addAll(formatList);
        }
        tableView.refresh();
    }//end changed()

}
