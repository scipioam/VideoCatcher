package pa.am.video_catcher.util;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * maven配置文件工具类
 * @author Alan Min
 * @since 2021/2/2
 */
public class MavenHelper {

    private final MavenXpp3Reader reader = new MavenXpp3Reader();

    private Model model;

    /**
     * 获取maven的pom配置文件对象
     * @return maven的pom配置文件对象
     * @throws IOException 读取文件错误
     * @throws XmlPullParserException 解析xml错误
     */
    public Model getModel() throws IOException, XmlPullParserException {
        if(model==null) {
            if ((new File("pom.xml")).exists())
                model = reader.read(new FileReader("pom.xml"));
            else
                model = reader.read(
                        new InputStreamReader(
                                getClass().getResourceAsStream("/META-INF/maven/de.scrum-master.stackoverflow/aspectj-introduce-method/pom.xml")
                        )
                );
        }
        return model;
    }

    /**
     * 获取maven版本号
     */
    public String getVersion() {
        String v;
        try {
            Model model = getModel();
            v = model.getVersion();
        }catch (Exception e) {
            v = null;
            e.printStackTrace();
        }
        return v;
    }

}
