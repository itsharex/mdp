package top.mddata.gateway.sop.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mddata.base.cache.redis.CacheResult;
import top.mddata.base.cache.repository.CacheOps;
import top.mddata.base.model.cache.CacheKey;
import top.mddata.common.cache.open.AccessTokenCkBuilder;
import top.mddata.common.cache.open.AppApiCkBuilder;
import top.mddata.common.cache.open.AppByAppKeyCkBuilder;
import top.mddata.common.cache.open.AppCkBuilder;
import top.mddata.common.cache.open.AppKeysCkBuilder;
import top.mddata.common.enumeration.BooleanEnum;
import top.mddata.gateway.sop.common.ApiDto;
import top.mddata.gateway.sop.common.AppDto;
import top.mddata.gateway.sop.manager.AppManager;
import top.mddata.open.entity.admin.App;
import top.mddata.open.entity.admin.AppGroupRel;
import top.mddata.open.entity.admin.AppKeys;
import top.mddata.open.entity.admin.GroupApiRel;
import top.mddata.open.entity.admin.ScopeGroup;
import top.mddata.open.mapper.admin.AppKeysMapper;
import top.mddata.open.mapper.admin.AppMapper;
import top.mddata.open.mapper.admin.GroupApiRelMapper;

import java.util.List;

/**
 *
 * @author henhen
 * @since 2026/1/6 16:47
 */
@Service
public class AppManagerImpl implements AppManager {
    @Resource
    private AppMapper mapper;
    @Resource
    private AppKeysMapper appKeysMapper;
    @Resource
    private GroupApiRelMapper groupApiRelMapper;
    @Resource
    private CacheOps cacheOps;

    @Override
    @Transactional(readOnly = true)
    public AppDto getByAppKey(String appKey) {
        CacheKey idKey = AppByAppKeyCkBuilder.builder(appKey);
        CacheResult<Long> appIdCache = cacheOps.get(idKey, (k) -> {
            App app = mapper.selectOneByQuery(QueryWrapper.create().eq(App::getAppKey, appKey));
            return app != null ? app.getId() : null;
        });

        if (appIdCache.isNullVal() || appIdCache.getValue() == null) {
            return null;
        }
        Long appId = appIdCache.asLong();
        if (appId == null) {
            return null;
        }

        CacheKey entityKey = AppCkBuilder.builder(appId);
        CacheResult<App> apiCache = cacheOps.get(entityKey, (k) -> mapper.selectOneById(appId));

        return BeanUtil.toBean(apiCache.getValue(), AppDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long id, ApiDto apiDto) {
        // 通用接口都可以访问
        if (BooleanEnum.FALSE.eq(apiDto.getPermission())) {
            return true;
        }
        CacheKey idKey = AppApiCkBuilder.builder(id);
        CacheResult<List<Long>> apiIdListCache = cacheOps.get(idKey, (k) -> {
            Iterable<QueryColumn> queryColumns = QueryMethods.defaultColumns(GroupApiRel.class);
            QueryWrapper wrapper = QueryWrapper.create().select(queryColumns).from(GroupApiRel.class)
                    .innerJoin(ScopeGroup.class).on(ScopeGroup::getId, GroupApiRel::getGroupId).and(ScopeGroup::getState).eq(true)
                    .innerJoin(AppGroupRel.class).on(AppGroupRel::getGroupId, ScopeGroup::getId)
                    .where(AppGroupRel::getAppId).eq(id);
            List<GroupApiRel> list = groupApiRelMapper.selectListByQuery(wrapper);
            return list.stream().map(GroupApiRel::getApiId).distinct().toList();
        });
        List<Long> apiIdList = apiIdListCache.asList();
        return CollUtil.contains(apiIdList, apiDto.getId());
    }

    @Override
    public String getAppSecret(Long appId) {
        CacheKey entityKey = AppCkBuilder.builder(appId);
        CacheResult<App> result = cacheOps.get(entityKey, k -> mapper.selectOneById(appId));
        App app = result.getValue();
        return app != null ? app.getAppSecret() : null;
    }

    @Override
    public String getPublicKeyApp(Long appId) {
        CacheKey keysKey = AppKeysCkBuilder.builder(appId);
        CacheResult<AppKeys> keysResult = cacheOps.get(keysKey, k ->
                appKeysMapper.selectOneByQuery(QueryWrapper.create().eq(AppKeys::getAppId, appId)));
        AppKeys keys = keysResult.getValue();
        return keys != null ? keys.getPublicKeyApp() : null;
    }

    @Override
    public Long getAppIdByAccessToken(String appKey, String accessToken) {
        if (appKey == null || accessToken == null) {
            return null;
        }
        CacheKey tokenKey = AccessTokenCkBuilder.builder(appKey, accessToken);
        CacheResult<Long> result = cacheOps.get(tokenKey);
        return result.isNullVal() ? null : result.asLong();
    }
}
