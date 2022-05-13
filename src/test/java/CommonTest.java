import org.junit.Test;

import java.io.File;

/**
 * @author Alan Min
 * @since 2021/2/22
 */
public class CommonTest {

    /**
     * 删除解密的片段
     */
    @Test
    public void test0() {
        File dir = new File("E:\\temp\\under_ts");
        File[] files = dir.listFiles();
        if(files == null) {
            return;
        }
        for (File file : files) {
            if(!file.getName().contains("_undec")) {
                System.out.println(file.getName());
                file.delete();
            }
        }
    }

}
