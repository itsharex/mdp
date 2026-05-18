package top.mddata.base.log.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.InvalidConfigException;
import org.lionsoul.ip2region.service.Ip2Region;
import org.lionsoul.ip2region.xdb.InetAddressException;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.XdbException;

import java.io.IOException;

/**
 * 根据ip查询地址
 *
 * @author henhen6
 * @date 2019/10/30
 */
@Slf4j
public final class AddressUtil {

    private static Searcher searcher = null;

//    static {
//        try {
//            URL resource = AddressUtil.class.getResource("/ip2region/ip2region.xdb");
//            if (resource != null) {
//                String dbPath = resource.getPath();
//                File file = new File(dbPath);
//                if (!file.exists()) {
//                    String tmpDir = System.getProperties().getProperty(StrPool.JAVA_TEMP_DIR);
//                    dbPath = tmpDir + "ip2region.xdb";
//                    file = new File(dbPath);
//                    String classPath = "classpath:ip2region/ip2region.xdb";
//                    InputStream resourceAsStream = ResourceUtil.getStreamSafe(classPath);
//                    if (resourceAsStream != null) {
//                        FileUtils.copyInputStreamToFile(resourceAsStream, file);
//                    }
//                }
//                // 1、从 dbPath 加载整个 xdb 到内存。
//                LongByteArray cBuff = Searcher.loadContentFromFile(dbPath);
//                searcher = Searcher.newWithBuffer(cBuff);
//
//                log.info("bean [{}]", searcher);
//            }
//        } catch (Exception e) {
//            log.error("init ip region error", e);
//        }
//    }

    private AddressUtil() {
    }

    /**
     * 解析IP
     *
     * @param ip ip
     * @return 地区
     */
    public static String getRegion(String ip) {
        try {
            //db
            if (searcher == null || StrUtil.isEmpty(ip)) {
                log.error("DbSearcher is null");
                return StrUtil.EMPTY;
            }
            long startTime = System.currentTimeMillis();

            String result = searcher.search(ip);
            long endTime = System.currentTimeMillis();
            log.debug("region use time[{}] result[{}]", endTime - startTime, result);
            return result;

        } catch (Exception e) {
            log.error("根据ip查询地区失败:", e);
        }
        return StrUtil.EMPTY;
    }


    public static void main(String[] args) throws IOException, InvalidConfigException, XdbException, InetAddressException, InterruptedException {

// 1, 创建 v4 的配置：指定缓存策略和 v4 的 xdb 文件路径
        final Config v4Config = Config.custom()
                .setCachePolicy(Config.VIndexCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                .setSearchers(15)                       // 设置初始化的查询器数量
                // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                // .setXdbInputStream(InputStream)      // 设置 v4 xdb 文件的 inputstream 对象
                // .setXdbFile(File)                    // 设置 v4 xdb File 对象
                // .setFairLock(boolean)                // 设置 ReentrantLock 是否使用公平锁
                .setXdbPath("ip2region v4 xdb path")    // 设置 v4 xdb 文件的路径
                .asV4();    // 指定为 v4 配置

// 2, 创建 v6 的配置：指定缓存策略和 v6 的 xdb 文件路径
        final Config v6Config = Config.custom()
                .setCachePolicy(Config.VIndexCache)     // 指定缓存策略: NoCache / VIndexCache / BufferCache
                .setSearchers(15)                       // 设置初始化的查询器数量
                // .setCacheSliceBytes(int)             // 设置缓存的分片字节数，默认为 50MiB
                // .setXdbInputStream(InputStream)      // 设置 v6 xdb 文件的 inputstream 对象
                // .setXdbFile(File)                    // 设置 v6 xdb File 对象
                // .setFairLock(boolean)                // 设置 ReentrantLock 是否使用公平锁
                .setXdbPath("ip2region v6 xdb path")    // 设置 v6 xdb 文件的路径
                .asV6();    // 指定为 v6 配置

// 备注：Xdb 三种初始化输入的优先级：XdbInputStream -> XdbFile -> XdbPath
// setXdbInputStream 仅方便使用者从 jar 包中加载 xdb 文件内容，这时 cachePolicy 只能设置为 Config.BufferCache

// 3，通过上述配置创建 Ip2Region 查询服务
        final Ip2Region ip2Region = Ip2Region.create(v4Config, v6Config);

// 4，导出 ip2region 服务作为全局变量，进行双版本的IP地址的并发查询，例如：
        final String v4Region = ip2Region.search("113.92.157.29");                          // 进行 IPv4 查询
        final String v6Region = ip2Region.search("240e:3b7:3272:d8d0:db09:c067:8d59:539e"); // 进行 IPv6 查询

// 5，在服务需要关闭的时候，同时关闭 ip2region 查询服务
// 备注：close 方法只需要在整个服务关闭的时候关闭，查询途中不需要操作
        ip2Region.close();
    }
}
