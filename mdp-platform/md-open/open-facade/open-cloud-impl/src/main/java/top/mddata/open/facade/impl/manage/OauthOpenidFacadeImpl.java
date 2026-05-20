package top.mddata.open.facade.impl.manage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.open.vo.admin.OauthOpenidVo;
import top.mddata.open.api.manage.OauthOpenidApi;
import top.mddata.open.manage.facade.OauthOpenidFacade;

/**
 *
 * @author henhen6
 * @since 2025/8/22 12:43
 */
@Service
@RequiredArgsConstructor
public class OauthOpenidFacadeImpl implements OauthOpenidFacade {
    private final OauthOpenidApi oauthOpenidApi;

    @Override
    public R<OauthOpenidVo> getByAppKeyAndUserId(String appKey, Long userId) {
        return oauthOpenidApi.getByAppKeyAndUserId(appKey, userId);
    }
}
