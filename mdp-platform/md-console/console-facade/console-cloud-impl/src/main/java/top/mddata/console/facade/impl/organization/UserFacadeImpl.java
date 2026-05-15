package top.mddata.console.facade.impl.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.common.entity.User;
import top.mddata.console.facade.api.organization.UserApi;
import top.mddata.console.organization.facade.UserFacade;

/**
 *
 * @author henhen
 * @since 2026/1/16 21:05
 */
@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    private final UserApi userApi;

    @Override
    public void registerByEmail(User ssoUser) {
        userApi.registerByEmail(ssoUser);
    }

    @Override
    public void registerByPhone(User ssoUser) {
        userApi.registerByPhone(ssoUser);
    }

    @Override
    public void registerByUsername(User defUser) {
        userApi.registerByUsername(defUser);
    }
}
