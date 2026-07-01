package top.mddata.common.cache.open;

import top.mddata.base.model.cache.CacheKey;
import top.mddata.base.model.cache.CacheKeyBuilder;
import top.mddata.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 访问令牌
 * {appKey}:{token} -> appId
 * <p>
 * 复合 key 将 token 与 appKey 物理绑定，防止攻击者
 * 用 A 应用的 token 冒充 B 应用调用接口。
 *
 * @author henhen
 * @since 2026/7/1
 */
public class AccessTokenCkBuilder implements CacheKeyBuilder {

    /**
     * accessToken 默认有效期：2 小时
     */
    public static final Duration DEFAULT_EXPIRE = Duration.ofHours(2);

    private static final String KEY_SEPARATOR = ":";

    /**
     * 构造复合缓存 key：{appKey}:{token}
     *
     * @param appKey 应用标识
     * @param token  访问令牌
     * @return key
     */
    public static CacheKey builder(String appKey, String token) {
        return new AccessTokenCkBuilder().key(appKey + KEY_SEPARATOR + token);
    }

    @Override
    public String getTable() {
        return CacheKeyTable.Open.ACCESS_TOKEN;
    }

    @Override
    public Duration getExpire() {
        return DEFAULT_EXPIRE;
    }
}
