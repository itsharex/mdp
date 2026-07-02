package top.mddata.sdk.simple.request.token;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取访问令牌请求参数
 *
 * @author henhen
 * @since 2026/7/2
 */
public class AccessTokenGetDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用标识
     */
    private String appKey;

    /**
     * 应用秘钥
     */
    private String appSecret;

    /**
     * 是否强制刷新（设置为true时，废弃旧token并颁发新token）
     */
    private Boolean forceRefresh;

    public String getAppKey() {
        return appKey;
    }

    public AccessTokenGetDto setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public AccessTokenGetDto setAppSecret(String appSecret) {
        this.appSecret = appSecret;
        return this;
    }

    public Boolean getForceRefresh() {
        return forceRefresh;
    }

    public AccessTokenGetDto setForceRefresh(Boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
        return this;
    }
}
