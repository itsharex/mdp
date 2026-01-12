package top.mddata.open.manage.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.open.admin.dto.NotifyInfoDto;
import top.mddata.open.admin.service.NotifyInfoService;
import top.mddata.open.manage.facade.NotifyAndEventPushFacade;

/**
 *
 * @author henhen
 * @since 2026/1/13 00:09
 */
@Service
@RequiredArgsConstructor
public class NotifyAndEventPushFacadeImpl implements NotifyAndEventPushFacade {
    private final NotifyInfoService notifyInfoService;

    @Override
    public R<Long> apiCallNotify(NotifyInfoDto request) {
        return notifyInfoService.notify(request);
    }

    @Override
    public R<Long> eventPush(NotifyInfoDto request) {
        return null;
    }
}
