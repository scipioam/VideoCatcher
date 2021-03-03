package pa.am.video_catcher.ui;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import pa.am.video_catcher.bean.ui.FormatVO;
import pa.am.video_catcher.bean.video.BiliPage;

import java.util.List;
import java.util.Map;

/**
 * B站分p下载的下拉框变化监听器
 * @author Alan Min
 * @since 2021/2/26
 */
public class BiliPageChangeListener implements ChangeListener<BiliPage> {

    private final JFXTreeTableView<FormatVO> tableView;
    private final Map<Long, List<FormatVO>> formatListMap;
    private final ObservableList<FormatVO> currentFormatList;

    public BiliPageChangeListener(JFXTreeTableView<FormatVO> tableView, Map<Long, List<FormatVO>> formatListMap, ObservableList<FormatVO> currentFormatList) {
        this.tableView = tableView;
        this.formatListMap = formatListMap;
        this.currentFormatList = currentFormatList;
    }

    @Override
    public void changed(ObservableValue<? extends BiliPage> observable, BiliPage oldValue, BiliPage newValue) {
        if(newValue==null) {
            return;
        }
        List<FormatVO> formatList = formatListMap.get(newValue.getCid());
        currentFormatList.clear();
        if(formatList!=null) {
            currentFormatList.addAll(formatList);
        }
        tableView.refresh();
    }//end changed()

}
