import org.junit.Test;
import pa.am.video_catcher.catcher.m3u8.DownloadListener;
import pa.am.video_catcher.catcher.m3u8.M3u8Catcher;
import pa.am.video_catcher.catcher.m3u8.bean.ErrorVO;
import pa.am.video_catcher.catcher.m3u8.bean.M3u8VO;

import java.util.List;

/**
 * @author Alan Min
 * @since 2021/2/2
 */
public class TsTest {

    //反恐特警组第四季第二集，一集41分34秒，500多个ts片段
    private final String URL_0 = "https://you.tube-kuyun.com/20201112/28917_3d0b4cbb/index.m3u8?sign=c5ba13730a1c61f4985db1f98c4ea5a1";
    //卡米拉第一季第一集，一集2多分钟，38个ts片段
    private final String URL_1 = "https://gaoqing.bibi-baidu.com/20190702/3631_2e7fdcb2/index.m3u8?sign=1c06192485dd725a15db83f3f4149d3f";

    /**
     * 获取ts流的信息
     */
    @Test
    public void testGetTsContent()
    {
        M3u8Catcher catcher = new M3u8Catcher();
        M3u8VO vo = catcher.getTsContent(URL_0,2);
        System.out.println(vo);
    }

    /**
     * 下载并合并
     */
    @Test
    public void testCatch()
    {
        String dir= "E:\\A_ts_test";
        String fileName = "test";

        M3u8Catcher catcher = new M3u8Catcher();
        catcher.setDir(dir);
        catcher.setFileName(fileName);

        //设置处理进程监听器
        catcher.setDownloadListener(new DownloadListener() {
            @Override
            public void onProcessing(int finishedFileCount, int totalFileCount) {
                System.out.println("处理进度:"+finishedFileCount+"/"+totalFileCount);
            }

            @Override
            public void onFinishedThread(int totalFileCount, List<ErrorVO> errorVOList) {

            }
            @Override
            public void onTotalThreadFinished() {

            }
        });

        try {
            catcher.doCatch(URL_1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 平均分配算法的测试
     */
    @Test
    public void averageAllocation()
    {
        int threadCount = 20;
        int taskCount = 438;
        int index = 1;

        int remainder = taskCount % threadCount;
        int q = taskCount / threadCount;
        for(int i=0; i<remainder; i++) {
            System.out.println("["+index+"]thread:"+(q+1));
            index++;
        }

        for(int j=0;j<(threadCount-remainder);j++) {
            System.out.println("["+index+"]thread:"+q);
            index++;
        }
    }

}
