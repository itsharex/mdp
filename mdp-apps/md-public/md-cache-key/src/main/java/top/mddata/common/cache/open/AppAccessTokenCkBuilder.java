package top.mddata.common.cache.open;

import top.mddata.base.model.cache.CacheKey;
import top.mddata.base.model.cache.CacheKeyBuilder;
import top.mddata.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 应用 → 访问令牌 反查索引
 * appKey -> token
 * <p>
 * 用于 forceRefresh 时废弃旧 token。
 * 与 AccessTokenCkBuilder（{appKey}:{token}→appId）配对使用。
 *
 * @author henhen
 * @since 2026/7/1
 */
public class AppAccessTokenCkBuilder implements CacheKeyBuilder {

    /**
     * 构造器
     *
     * @param appKey 应用标识
     * @return key
     */
    public static CacheKey builder(String appKey) {
        return new AppAccessTokenCkBuilder().key(appKey);
    }

    @Override
    public String getTable() {
        return CacheKeyTable.Open.ACCESS_TOKEN + ":app";
    }

    @Override
    public Duration getExpire() {
        return AccessTokenCkBuilder.DEFAULT_EXPIRE;
    }
}
