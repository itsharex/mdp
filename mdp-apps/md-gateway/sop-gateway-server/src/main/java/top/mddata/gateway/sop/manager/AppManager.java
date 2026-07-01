package top.mddata.gateway.sop.manager;

import top.mddata.gateway.sop.common.ApiDto;
import top.mddata.gateway.sop.common.AppDto;

/**
 *
 * @author henhen
 * @since 2026/1/6 16:44
 */
public interface AppManager {
    /**
     * 根据appKey获取app信息
     *
     * @param appKey appKey
     * @return AppDto
     */
    AppDto getByAppKey(String appKey);

    /**
     * 判断当前应用是否有权限访问该开放接口
     *
     * @param id      应用ID
     * @param apiDto  开放接口
     * @return boolean
     */
    boolean hasPermission(Long id, ApiDto apiDto);

    /**
     * 获取应用共享密钥（用于验证 accessToken 获取请求的身份）
     *
     * @param id 应用ID
     * @return appSecret
     */
    String getAppSecret(Long id);

    /**
     * 获取开发者应用公钥（用于 RSA2 签名校验）
     *
     * @param appId 应用ID
     * @return 开发者应用公钥
     */
    String getPublicKeyApp(Long appId);

    /**
     * 校验 accessToken 是否有效，返回对应的应用ID。
     * <p>
     * 使用 {appKey}:{token} 复合 key 查询，确保 token 与 appKey 绑定，
     * 防止攻击者用 A 应用的 token 冒充 B 应用调用接口。
     *
     * @param appKey      应用标识
     * @param accessToken 访问令牌
     * @return 应用ID；无效返回 null
     */
    Long getAppIdByAccessToken(String appKey, String accessToken);
}
