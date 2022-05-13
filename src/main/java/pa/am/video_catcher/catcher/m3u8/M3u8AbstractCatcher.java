package pa.am.video_catcher.catcher.m3u8;

import com.github.ScipioAM.scipio_utils_common.StringUtil;
import com.github.ScipioAM.scipio_utils_net.http.HttpUtil;
import com.github.ScipioAM.scipio_utils_net.http.bean.ResponseResult;
import pa.am.video_catcher.catcher.m3u8.bean.M3u8VO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;

/**
 * @author Alan Min
 * @since 2021/2/7
 */
public abstract class M3u8AbstractCatcher {

    /**
     * 执行下载并合并
     * @param m3u8Url 要下载的m3u8链接
     */
    public abstract void doCatch(String m3u8Url);

    /**
     * 获取所有的ts片段下载链接和密钥
     * @param url m3u8的链接(或第一个链接)
     * @param retryLimit 重试次数的上限
     * @return 下载所需的各种信息，包括下载链接和可能有的密钥
     */
    public M3u8VO getTsContent(String url, int retryLimit) {
        M3u8VO vo = new M3u8VO();
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.setDefaultUserAgent();
        //发起请求获取链接内容
        String content = getUrlContent(httpUtil,url,retryLimit);
        //判断是否为m3u8链接
        if (!content.contains("#EXTM3U"))
            throw new M3u8Exception("[" + url + "] is not a m3u8 link!");

        String[] arr = content.split("\\n");
        String urlPrefix = url.substring(0, url.lastIndexOf("/") + 1);
        for(int i=0;i<arr.length;i++) {
            String s = arr[i];
            //密钥字段
            if (s.contains("#EXT-X-KEY")) {
                vo.setEncrypted(true);
                getTsKey(s,vo,urlPrefix,retryLimit,httpUtil);//发起请求获取密钥文件的内容
            }
            //如果含有此字段，则说明只有一层m3u8链接，其下一个元素就是ts的相对路径
            else if(s.contains("#EXTINF")) {
                String tsUrl = arr[i+1];
                vo.addTsUrl(StringUtil.isHttpUrl(tsUrl) ? tsUrl : urlPrefix + tsUrl);
            }
            //如果含有此字段，则说明ts片段链接需要从第二个m3u8链接获取
            else if (s.contains(".m3u8")) {
                //如果是个完整的http的url，则直接发起请求，否则进行拼接
                String secondUrl = (StringUtil.isHttpUrl(s) ? s : urlPrefix + s);
                getTsUrlFromSecondUrl(secondUrl,vo,retryLimit,httpUtil);//发起请求
                break;
            }
        }//end for
        return vo;
    }

    /**
     * 从第二个m3u8链接获取所有ts片段的url
     * @param secondUrl 第二个m3u8链接
     * @param vo m3u8相关信息的数据对象
     * @param retryLimit 重试次数的上限
     */
    private void getTsUrlFromSecondUrl(String secondUrl, M3u8VO vo, int retryLimit, HttpUtil httpUtil) {
        String content = getUrlContent(httpUtil,secondUrl,retryLimit);//发起请求获取第二个链接的内容
        //判断是否为m3u8链接
        if (!content.contains("#EXTM3U"))
            throw new M3u8Exception("[" + secondUrl + "] is not a m3u8 link!");

        String urlPrefix = secondUrl.substring(0, secondUrl.lastIndexOf("/") + 1);
        String[] arr = content.split("\\n");
        //将ts片段链接加入set集合
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            //密钥字段
            if (s.contains("#EXT-X-KEY")) {
                vo.setEncrypted(true);
                getTsKey(s,vo,urlPrefix,retryLimit,httpUtil);//发起请求获取密钥文件的内容
            }
            //ts片段的url字段
            else if (s.contains("#EXTINF")) {
                String tsUrl = arr[i+1];
                vo.addTsUrl(StringUtil.isHttpUrl(tsUrl) ? tsUrl : urlPrefix + tsUrl);
            }
        }//end for
    }//end getTsUrl()


    /**
     * 获取ts解密的密钥和算法
     * @param s 带有[#EXT-X-KEY]字样的字符串行
     *          样例1：#EXT-X-KEY:METHOD=AES-128,URI="https://j-island.net/movie/hls_key/s/857401e309d8a032c3bb18f4b09b8db2/?f=jj_20190401_hihijets_004",IV=0xaa3dcf6a7acb92ff4fb08d9b3b3d6f51
     *          样例2：#EXT-X-KEY:METHOD=AES-128,URI="key.key"
     * @param vo m3u8相关信息的数据对象
     * @param urlPrefix m3u8的url前缀（用于对相对路径的拼接）
     * @param retryLimit 重试次数的上限
     */
    private void getTsKey(String s, M3u8VO vo, String urlPrefix, int retryLimit, HttpUtil httpUtil) {
        String keyUrl = null;
        String[] arr = s.split(",");
        if (arr[0].contains("METHOD")) {
            String method = arr[0].split("=", 2)[1];
            vo.setMethod(method);
        }
        if (arr[1].contains("URI")) {
            keyUrl = arr[1].split("=", 2)[1];
        }
        if (arr.length>=3) {
            if(arr[2].contains("IV")) {
                String ivStr = arr[2].split("=", 2)[1];
                vo.setIvStr(ivStr);
            }
        }

        if(StringUtil.isNotNull(keyUrl)) {
            keyUrl = keyUrl.replace("\"", "");//去掉引号
            if(StringUtil.isHttpUrl(keyUrl)) {
               vo.setKeyUrl(keyUrl);
            }
            else {
               keyUrl = urlPrefix + keyUrl;
            }
            //发起请求获取密钥文件
            String key = getUrlContent(httpUtil,keyUrl,retryLimit).replaceAll("\\s+", "");//去掉空格
            vo.setKey(key);
        }
    }//end getKey()

    /**
     * 获取http请求的内容
     * @param url http链接
     * @param retryLimit 重试次数的上限
     * @return 内容，如果获取失败则抛出M3u8Exception异常
     */
    protected String getUrlContent(HttpUtil httpUtil, String url, int retryLimit) {
        int count = 0;
        if(httpUtil==null) {
            httpUtil = new HttpUtil();
            httpUtil.setDefaultUserAgent();//设置UA
        }
        String urlContent = null;
        while (count<retryLimit) {
            ResponseResult response = httpUtil.get(url);
            if(response.getResponseCode()<=-1) {
                count++;
            }
            else {
                urlContent = response.getData();
                break;
            }
        }//end while
        if(count>=retryLimit) {
            throw new M3u8Exception("Connect timed out");
        }
        return urlContent;
    }//end getUrlContent()

    protected String getUrlContent(String url, int retryLimit) {
        return getUrlContent(null,url,retryLimit);
    }

    /**
     * 合并所有ts片段为一个整体的文件
     * @param tsFileSet ts文件列表
     * @param rootDir 根目录
     * @param tempDir 临时下载目录(所有ts文件在这里)
     * @param fileName 整体文件的文件名
     * @param fileSuffix 整体文件的后缀
     * @param isDeleteTs 是否在合并完后删除ts片段，为true代表是
     * @return 整体文件本身
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File mergeTsFiles(Set<File> tsFileSet, String rootDir, File tempDir, String fileName, String fileSuffix, boolean isDeleteTs) {
        File finalFile = new File(rootDir + File.separator + fileName + fileSuffix);
        FileOutputStream out = null;
        try {
            if (finalFile.exists()) {
                finalFile.delete();
            }
            finalFile.createNewFile();
            out = new FileOutputStream(finalFile);
            //开始合并
            int len;
            byte[] b = new byte[4096];//缓冲池8kb
            for(File tsFile : tsFileSet) {
                FileInputStream in = new FileInputStream(tsFile);
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                in.close();
                out.flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out!=null)
                    out.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }//end finally

        //开始删除ts文件
        if(isDeleteTs) {
            //删除ts文件
            for(File tsFile : tsFileSet) {
                tsFile.delete();
            }
            //删除临时下载文件夹
            tempDir.delete();
        }
        return finalFile;
    }//end mergeTsFiles()

}
