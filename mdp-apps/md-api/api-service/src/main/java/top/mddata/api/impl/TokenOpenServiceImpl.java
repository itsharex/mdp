package top.mddata.api.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.gitee.sop.support.exception.OpenException;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import top.mddata.api.TokenOpenService;
import top.mddata.api.dto.AccessTokenDto;
import top.mddata.api.resp.AccessTokenResp;
import top.mddata.base.cache.redis.CacheResult;
import top.mddata.base.cache.repository.CacheOps;
import top.mddata.base.model.cache.CacheKey;
import top.mddata.common.cache.open.AccessTokenCkBuilder;
import top.mddata.common.cache.open.AppAccessTokenCkBuilder;
import top.mddata.common.enumeration.StoryMessageEnum;
import top.mddata.open.entity.admin.App;
import top.mddata.open.mapper.admin.AppMapper;

import java.time.Duration;

/**
 * 访问令牌接口实现
 * <p>
 * 通过 appKey + appSecret 颁发 accessToken，
 * 第三方后续使用 accessToken 调用其他开放接口。
 *
 * @author henhen
 * @since 2026/7/1
 */
@DubboService
@Slf4j
@Service
public class TokenOpenServiceImpl implements TokenOpenService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private CacheOps cacheOps;

    @Override
    public AccessTokenResp get(AccessTokenDto dto) {
        try {
            // 1. 校验应用凭证
            App app = appMapper.selectOneByQuery(
                    QueryWrapper.create().eq(App::getAppKey, dto.getAppKey()));
            if (app == null) {
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "无效的应用标识");
            }
            if (!StrUtil.equals(app.getAppSecret(), dto.getAppSecret())) {
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "应用秘钥错误");
            }
            if (app.getState() == null || !app.getState()) {
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "应用已被禁用");
            }

            Long appId = app.getId();

            // 2. 非强制刷新：优先返回已存在的未过期 token
            if (!Boolean.TRUE.equals(dto.getForceRefresh())) {
                CacheKey appKey = AppAccessTokenCkBuilder.builder(appId);
                CacheResult<String> existToken = cacheOps.get(appKey);
                if (!existToken.isNullVal() && StrUtil.isNotBlank(existToken.getValue())) {
                    String token = existToken.getValue();
                    // 确认 token 仍有效（存在 token→appId 映射）
                    CacheKey tokenKey = AccessTokenCkBuilder.builder(token);
                    if (Boolean.TRUE.equals(cacheOps.exists(tokenKey))) {
                        return AccessTokenResp.builder()
                                .accessToken(token)
                                .expiresIn(AccessTokenCkBuilder.DEFAULT_EXPIRE.getSeconds())
                                .build();
                    }
                }
            }

            // 3. 强制刷新或无有效 token：生成新 token
            String newToken = generateToken(appId);
            Duration expire = AccessTokenCkBuilder.DEFAULT_EXPIRE;

            // 4. 废弃旧 token（如果存在）
            CacheKey appKey = AppAccessTokenCkBuilder.builder(appId);
            CacheResult<String> oldTokenResult = cacheOps.get(appKey);
            if (!oldTokenResult.isNullVal() && StrUtil.isNotBlank(oldTokenResult.getValue())) {
                cacheOps.del(AccessTokenCkBuilder.builder(oldTokenResult.getValue()));
            }

            // 5. 写入新 token
            cacheOps.set(AccessTokenCkBuilder.builder(newToken), appId);
            cacheOps.set(appKey, newToken);

            return AccessTokenResp.builder()
                    .accessToken(newToken)
                    .expiresIn(expire.getSeconds())
                    .build();
        } catch (OpenException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取accessToken失败, dto={}", dto, e);
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "获取令牌失败：" + e.getMessage());
        }
    }

    /**
     * 生成 accessToken
     * 采用两段 SHA-256 拼接输出 128 位十六进制串，满足 512+ 位长度要求
     */
    private String generateToken(Long appId) {
        String raw1 = appId + ":" + IdUtil.fastSimpleUUID() + ":" + System.nanoTime();
        String raw2 = IdUtil.fastSimpleUUID() + ":" + System.nanoTime() + ":" + appId;
        return SecureUtil.sha256(raw1) + SecureUtil.sha256(raw2);
    }
}
