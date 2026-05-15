package top.mddata.open.facade.impl.manage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.mddata.base.base.R;
import top.mddata.open.admin.vo.OauthUnionidVo;
import top.mddata.open.api.manage.OauthUnionidApi;
import top.mddata.open.manage.facade.OauthUnionidFacade;

/**
 *
 * @author henhen6
 * @since 2025/8/22 00:07
 */
@Component
@RequiredArgsConstructor
public class OauthUnionidFacadeImpl implements OauthUnionidFacade {
    private final OauthUnionidApi oauthUnionidApi;

    @Override
    public R<OauthUnionidVo> getBySubjectIdAndUserId(Long subjectId, Long userId) {
        return oauthUnionidApi.getBySubjectIdAndUserId(subjectId, userId);
    }
}
