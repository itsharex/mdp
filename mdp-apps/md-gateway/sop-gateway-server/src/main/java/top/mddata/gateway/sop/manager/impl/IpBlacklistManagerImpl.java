package top.mddata.gateway.sop.manager.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.utils.IpUtil;
import top.mddata.gateway.sop.common.AppDto;
import top.mddata.gateway.sop.manager.IpBlacklistManager;

import java.util.List;

/**
 * IP访问控制管理实现
 * @author henhen
 * @since 2026/1/6 16:47
 */
@Slf4j
@Service
public class IpBlacklistManagerImpl implements IpBlacklistManager {

    @Override
    public boolean isAllowed(String ip, AppDto appDto) {
        // 标准化IP（IPv6回环地址转IPv4）
        String normalizedIp = IpUtil.normalizeLoopback(ip);

        // 1. 检查IP黑名单（TODO: 后续实现黑名单功能）
        // if (isInBlacklist(normalizedIp)) {
        //     return false;
        // }

        // 2. 检查IP白名单（如果配置了）
        if (appDto != null) {
            String allowIp = appDto.getAllowIp();
            if (StrUtil.isNotEmpty(allowIp)) {
                List<String> allowIpList = StrUtil.splitTrim(allowIp, ",");
                if (!allowIpList.isEmpty()) {
                    boolean matched = allowIpList.stream()
                            .anyMatch(pattern -> IpUtil.matchIp(pattern, normalizedIp));
                    if (!matched) {
                        log.warn("客户端IP:[{}]不在白名单中，应用:[{}], 允许IP:[{}]",
                                normalizedIp, appDto.getAppKey(), allowIp);
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
