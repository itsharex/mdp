package top.mddata.open.facade.impl.manage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.open.dto.admin.EventTriggerDto;
import top.mddata.open.dto.admin.NotifyInfoDto;
import top.mddata.open.entity.admin.EventTrigger;
import top.mddata.open.api.manage.EventTriggerApi;
import top.mddata.open.api.manage.NotifyInfoApi;
import top.mddata.open.manage.facade.NotifyAndEventPushFacade;

/**
 *
 * @author henhen
 * @since 2026/1/13 00:09
 */
@Service
@RequiredArgsConstructor
public class NotifyAndEventPushFacadeImpl implements NotifyAndEventPushFacade {
    private final NotifyInfoApi notifyInfoApi;
    private final EventTriggerApi eventTriggerApi;

    @Override
    public R<Long> apiCallNotify(NotifyInfoDto request) {
        return notifyInfoApi.notify(request);
    }

    @Override
    public R<EventTrigger> eventPush(EventTriggerDto request) {
        return eventTriggerApi.save(request);
    }
}
