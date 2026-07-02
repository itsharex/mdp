package top.mddata.sdk.simple.response.token;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取访问令牌响应
 *
 * @author henhen
 * @since 2026/7/2
 */
public class AccessTokenGetResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间（单位：秒）
     */
    private Long expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public AccessTokenGetResp setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public AccessTokenGetResp setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
