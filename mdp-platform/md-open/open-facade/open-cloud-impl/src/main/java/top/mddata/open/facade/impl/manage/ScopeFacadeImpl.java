package top.mddata.open.facade.impl.manage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.open.admin.vo.OauthScopeVo;
import top.mddata.open.api.manage.ScopeApi;
import top.mddata.open.manage.facade.OauthScopeFacade;

import java.util.List;

/**
 *
 * @author henhen6
 * @since 2025/8/24 23:43
 */
@Service
@RequiredArgsConstructor
public class ScopeFacadeImpl implements OauthScopeFacade {
    private final ScopeApi soopeApi;

    @Override
    public R<List<OauthScopeVo>> getScopeListByCode(List<String> scopes) {
        return soopeApi.getScopeListByCode(scopes);
    }

    @Override
    public R<List<OauthScopeVo>> listByAppId(Long appId) {
        return soopeApi.listByAppId(appId);
    }
}
