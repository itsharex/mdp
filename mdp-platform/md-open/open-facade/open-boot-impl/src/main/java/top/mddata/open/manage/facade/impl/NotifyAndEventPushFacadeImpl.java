package top.mddata.open.manage.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.open.admin.dto.EventTriggerDto;
import top.mddata.open.admin.dto.NotifyInfoDto;
import top.mddata.open.admin.entity.EventTrigger;
import top.mddata.open.admin.service.EventTriggerService;
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
    private final EventTriggerService eventTriggerService;

    @Override
    public R<Long> apiCallNotify(NotifyInfoDto request) {
        return notifyInfoService.notify(request);
    }

    @Override
    public R<EventTrigger> eventPush(EventTriggerDto request) {
        return R.success(eventTriggerService.save(request));
    }
}
