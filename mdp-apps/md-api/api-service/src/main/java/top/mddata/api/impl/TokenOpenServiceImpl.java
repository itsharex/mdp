package top.mddata.api.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.gitee.sop.support.exception.OpenException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import top.mddata.api.TokenOpenService;
import top.mddata.api.dto.AccessTokenDto;
import top.mddata.api.resp.AccessTokenResp;
import top.mddata.base.base.R;
import top.mddata.base.cache.redis.CacheResult;
import top.mddata.base.cache.repository.CacheOps;
import top.mddata.base.model.cache.CacheKey;
import top.mddata.common.cache.open.AccessTokenCkBuilder;
import top.mddata.common.cache.open.AppAccessTokenCkBuilder;
import top.mddata.common.enumeration.StoryMessageEnum;
import top.mddata.open.facade.admin.AppFacade;
import top.mddata.open.vo.admin.AppVo;

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
    private AppFacade appFacade;

    @Resource
    private CacheOps cacheOps;

    @Override
    public AccessTokenResp get(AccessTokenDto dto) {
        try {
            // 1. 校验应用凭证（通过 Facade 远程调用，避免 api-service 直接访问 open 库）
            R<AppVo> appResult = appFacade.getAppByAppKey(dto.getAppKey());
            if (!Boolean.TRUE.equals(appResult.getIsSuccess()) || appResult.getData() == null) {
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "无效的应用标识");
            }
            AppVo app = appResult.getData();
            if (!StrUtil.equals(app.getAppSecret(), dto.getAppSecret())) {
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "应用秘钥错误");
            }
            if (app.getState() == null || !app.getState()) {
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "应用已被禁用");
            }

            String appKey = app.getAppKey();

            // 2. 非强制刷新：优先返回已存在的未过期 token
            if (!Boolean.TRUE.equals(dto.getForceRefresh())) {
                CacheKey reverseKey = AppAccessTokenCkBuilder.builder(appKey);
                CacheResult<String> existToken = cacheOps.get(reverseKey);
                if (!existToken.isNullVal() && StrUtil.isNotBlank(existToken.getValue())) {
                    String token = existToken.getValue();
                    // 确认 token 仍有效（存在复合 key 映射）
                    CacheKey tokenKey = AccessTokenCkBuilder.builder(appKey, token);
                    if (Boolean.TRUE.equals(cacheOps.exists(tokenKey))) {
                        return AccessTokenResp.builder()
                                .accessToken(token)
                                .expiresIn(AccessTokenCkBuilder.DEFAULT_EXPIRE.getSeconds())
                                .build();
                    }
                }
            }

            // 3. 强制刷新或无有效 token：生成新 token
            String newToken = generateToken(appKey);
            Duration expire = AccessTokenCkBuilder.DEFAULT_EXPIRE;

            // 4. 废弃旧 token（如果存在）
            CacheKey reverseKey = AppAccessTokenCkBuilder.builder(appKey);
            CacheResult<String> oldTokenResult = cacheOps.get(reverseKey);
            if (!oldTokenResult.isNullVal() && StrUtil.isNotBlank(oldTokenResult.getValue())) {
                cacheOps.del(AccessTokenCkBuilder.builder(appKey, oldTokenResult.getValue()));
            }

            // 5. 写入新 token（复合 key 绑定 appKey，防止跨应用冒用）
            cacheOps.set(AccessTokenCkBuilder.builder(appKey, newToken), app.getId());
            cacheOps.set(reverseKey, newToken);

            return AccessTokenResp.builder()
                    .accessToken(newToken)
                    .expiresIn(expire.getSeconds())
                    .build();
        } catch (OpenException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取accessToken失败, appKey={}", dto.getAppKey(), e);
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "获取令牌失败：" + e.getMessage());
        }
    }

    /**
     * 生成 accessToken
     * 采用两段 SHA-256 拼接输出 128 位十六进制串，满足 512+ 位长度要求
     */
    private String generateToken(String appKey) {
        String raw1 = appKey + ":" + IdUtil.fastSimpleUUID() + ":" + System.nanoTime();
        String raw2 = IdUtil.fastSimpleUUID() + ":" + System.nanoTime() + ":" + appKey;
        return SecureUtil.sha256(raw1) + SecureUtil.sha256(raw2);
    }
}
