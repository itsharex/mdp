package top.mddata.common.cache.workbench;


import top.mddata.base.model.cache.CacheKey;
import top.mddata.base.model.cache.CacheKeyBuilder;
import top.mddata.common.cache.CacheKeyTable;

import java.time.Duration;

/**
 * 找回密码 KEY
 * <p>
 *
 * @author henhen6
 * @date 2020/9/20 6:45 下午
 */
public class ForgetPasswordCacheKeyBuilder implements CacheKeyBuilder {
    public static CacheKey build(String token) {
        return new ForgetPasswordCacheKeyBuilder().key(token);
    }

    @Override
    public String getTable() {
        return CacheKeyTable.FORGET_PWD;
    }


    @Override
    public Duration getExpire() {
        return Duration.ofHours(2);
    }
}
