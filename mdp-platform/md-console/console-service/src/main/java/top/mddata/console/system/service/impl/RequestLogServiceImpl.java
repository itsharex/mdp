package top.mddata.console.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.OS;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.log.util.AddressUtil;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.base.utils.StrPool;
import top.mddata.common.entity.User;
import top.mddata.console.organization.service.UserService;
import top.mddata.console.system.entity.RequestLog;
import top.mddata.console.system.entity.RequestLogDetail;
import top.mddata.console.system.mapper.RequestLogMapper;
import top.mddata.console.system.service.RequestLogDetailService;
import top.mddata.console.system.service.RequestLogService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 请求日志 服务层实现。
 *
 * @author henhen6
 * @since 2025-11-12 16:21:25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RequestLogServiceImpl extends SuperServiceImpl<RequestLogMapper, RequestLog> implements RequestLogService {

    private final RequestLogDetailService requestLogDetailService;
    private final UserService userService;
    private static final Supplier<Stream<String>> BROWSER = () -> Stream.of(
            "Chrome", "Firefox", "Microsoft Edge", "Safari", "Opera"
    );
    private static final Supplier<Stream<String>> OPERATING_SYSTEM = () -> Stream.of(
            "Android", "Linux", "Mac OS X", "Ubuntu", "Windows 10", "Windows 8", "Windows 7", "Windows XP", "Windows Vista"
    );

    private static String simplifyOperatingSystem(String operatingSystem) {
        return OPERATING_SYSTEM.get().parallel().filter(b -> StrUtil.containsIgnoreCase(operatingSystem, b)).findAny().orElse(operatingSystem);
    }

    private static String simplifyBrowser(String browser) {
        return BROWSER.get().parallel().filter(b -> StrUtil.containsIgnoreCase(browser, b)).findAny().orElse(browser);
    }

    @Override
    protected RequestLog saveBefore(Object save) {
        RequestLog requestLog = super.saveBefore(save);
        if (requestLog.getUserId() != null) {
            User user = userService.getById(requestLog.getUserId());
            if (user != null) {
                requestLog.setUserName(user.getName());
            }
        }
        UserAgent userAgent = UserAgentUtil.parse(requestLog.getUa());
        Browser browser = userAgent.getBrowser();
        OS os = userAgent.getOs();
        String browserVersion = userAgent.getVersion();
        if (browser != null) {
            requestLog.setBrowserName(simplifyBrowser(browser.getName()));
        }
        if (browserVersion != null) {
            requestLog.setBrowserVersion(browserVersion);
        }
        if (os != null) {
            requestLog.setOs(simplifyOperatingSystem(os.getName()));
        }
        String ipLocation = isLocalHostIp(requestLog.getIpAddress()) ? "" : AddressUtil.getRegion(requestLog.getIpAddress());
        List<String> ipLocationArray = StrUtil.split(ipLocation, StrPool.PIPE);
        if (ipLocationArray.size() >= 5) {
            String country = ipLocationArray.get(0);    // 中国
            String region = ipLocationArray.get(1);    // 0
            String province = ipLocationArray.get(2);  // 广东省
            String city = ipLocationArray.get(3);      // 广州市
            String isp = ipLocationArray.get(4);       // 电信
            requestLog.setIpCountry(country);
            requestLog.setIpRegion(region);
            requestLog.setIpProvince(province);
            requestLog.setIpCity(city);
            requestLog.setIpIsp(isp);
        }

        return requestLog;
    }

    /**
     * 判断是否为本地IP地址的方法
     */
    private boolean isLocalHostIp(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.isLoopbackAddress();
        } catch (UnknownHostException e) {
            // 处理异常情况，如果无法解析IP地址，则不视为本地地址
            return false;
        }
    }

    @Override
    protected void saveAfter(Object save, RequestLog entity) {
        RequestLogDetail detail = BeanUtil.toBean(save, RequestLogDetail.class);
        detail.setId(entity.getId());
        requestLogDetailService.save(detail);
    }
}
