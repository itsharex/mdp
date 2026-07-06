package top.mddata.gateway.sop.manager;

import top.mddata.gateway.sop.common.AppDto;

/**
 * IP访问控制管理（黑名单+白名单）
 * @author henhen
 * @since 2026/1/6 16:44
 */
public interface IpBlacklistManager {
    /**
     * 检查IP是否允许访问
     * @param ip 客户端IP
     * @param appDto 应用信息（包含白名单配置）
     * @return true=允许访问，false=禁止访问
     */
    boolean isAllowed(String ip, AppDto appDto);
}
