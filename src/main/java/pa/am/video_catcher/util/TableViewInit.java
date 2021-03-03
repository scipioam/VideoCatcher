package pa.am.video_catcher.util;

import com.jfoenix.controls.JFXTreeTableColumn;
import javafx.beans.property.*;
import javafx.scene.control.TreeTableColumn;
import pa.am.video_catcher.bean.ui.ColumnType;
import pa.am.video_catcher.bean.ui.FormatVO;

/**
 * @author Alan Min
 * @since 2021/1/19
 */
public class TableViewInit {

    public static void initStrColumn(JFXTreeTableColumn<FormatVO,String> tc, ColumnType columnType) {
        tc.setCellValueFactory((TreeTableColumn.CellDataFeatures<FormatVO, String> param) ->{
            if(tc.validateValue(param))
                return getStrProperty(param.getValue().getValue(),columnType);
            else
                return tc.getComputedValue(param);
        });
    }

    //===============================================================

    private static StringProperty getStrProperty(FormatVO vo, ColumnType columnType) {
        StringProperty prop = null;
        switch (columnType)
        {
            case FORMAT_ID:
                prop = vo.formatIdProperty();
                break;
            case FORMAT_INFO:
                prop = vo.formatInfoProperty();
                break;
            case NOTE:
                prop = vo.noteProperty();
                break;
            case RESOLUTION:
                prop = vo.resolutionProperty();
                break;
            case EXTENSION:
                prop = vo.extensionProperty();
                break;
            case FILE_SIZE:
                prop = new SimpleStringProperty();
                prop.set(vo.getFileSize()<=0L ? "unknown" : vo.getFileSizeInfo());
                break;
            case FPS:
                prop = new SimpleStringProperty();
                prop.set(vo.getFps()+"");
                break;
            case CODEC:
                prop = vo.codecProperty();
                break;
        }
        return prop;
    }

}
