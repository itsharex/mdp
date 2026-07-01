package top.mddata.common.cache.open;

import top.mddata.base.model.cache.CacheKey;
import top.mddata.base.model.cache.CacheKeyBuilder;
import top.mddata.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 访问令牌
 * token -> appId
 *
 * @author henhen
 * @since 2026/7/1
 */
public class AccessTokenCkBuilder implements CacheKeyBuilder {

    /**
     * accessToken 默认有效期：2 小时
     */
    public static final Duration DEFAULT_EXPIRE = Duration.ofHours(2);

    /**
     * 构造器
     *
     * @param token 访问令牌
     * @return key
     */
    public static CacheKey builder(String token) {
        return new AccessTokenCkBuilder().key(token);
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
