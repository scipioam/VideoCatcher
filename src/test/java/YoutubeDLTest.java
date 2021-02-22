import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import com.sapher.youtubedl.mapper.VideoInfo;
import org.junit.Test;

/**
 * @author Alan Min
 * @since 2021/2/19
 */
public class YoutubeDLTest {

    private final String VIDEO_URL0 = "https://www.youtube.com/watch?v=wox1BCQrB-k&ab_channel=Gilou_Menfou";
    private final String VIDEO_URL1 = "https://www.bilibili.com/video/BV1LJ411d7g2?spm_id_from=333.851.b_62696c695f7265706f72745f67756f636875616e67.66";

    @Test
    public void testDownload()
    {
        // Destination directory
        String directory = "E:\\ATest";

        YoutubeDL.setExecutablePath("youtube-dl.exe");

        // Build request
        YoutubeDLRequest request = new YoutubeDLRequest(VIDEO_URL1, directory);
        request.setOption("ignore-errors");		// --ignore-errors
        request.setOption("output", "%(id)s");	// --output "%(id)s"
        request.setOption("retries", 10);		// --retries 10
//        request.setOption("format","bestaudio");

        // Make request and return response
        try {
            YoutubeDLResponse response = YoutubeDL.execute(request,
                    (progress, etaInSeconds) -> System.out.println(progress + "%")
            );

            // Response
            System.out.println("Video id: "+response.getId());
            String stdOut = response.getOut(); // Executable output
            System.out.println("stdOut:"+stdOut);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }//end test

    @Test
    public void testGetInfo()
    {
        try {
            VideoInfo info = YoutubeDL.getVideoInfo(VIDEO_URL1);
            System.out.println(info);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
