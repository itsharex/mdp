package top.mddata.console.organization.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.common.entity.User;
import top.mddata.console.organization.facade.UserFacade;
import top.mddata.console.organization.service.UserService;

/**
 *
 * @author henhen
 * @since 2026/1/16 21:05
 */
@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;

    @Override
    public void registerByEmail(User ssoUser) {
        userService.registerByEmail(ssoUser);
    }

    @Override
    public void registerByPhone(User ssoUser) {
        userService.registerByPhone(ssoUser);
    }

    @Override
    public void registerByUsername(User defUser) {
        userService.registerByUsername(defUser);
    }
}
