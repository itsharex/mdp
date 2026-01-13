package top.mddata.open.admin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.open.admin.entity.App;
import top.mddata.open.admin.entity.EventPush;
import top.mddata.open.admin.enumeration.ExecStatusEnum;
import top.mddata.open.admin.mapper.EventPushMapper;
import top.mddata.open.admin.properties.NotifyProperties;
import top.mddata.open.admin.service.AppService;
import top.mddata.open.admin.service.EventPushService;

/**
 * 事件推送任务 服务层实现。
 *
 * @author henhen6
 * @since 2026-01-12 21:28:36
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventPushServiceImpl extends SuperServiceImpl<EventPushMapper, EventPush> implements EventPushService {
    private final AppService appService;
    private final NotifyProperties notifyProperties;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByEventTrigger(String eventCode, Long triggerId, String requestData) {

        App app = null;

        EventPush eventPush = new EventPush();
        eventPush.setEventCode(eventCode);
        eventPush.setEventTriggerId(triggerId);
        eventPush.setAppId(app.getId());
        eventPush.setAppKey(app.getAppKey());
//        eventPush.setNotifyUrl(app.getno);

        eventPush.setRequestData(requestData);
        eventPush.setMaxRequestCnt(notifyProperties.getMaxRetry());
        eventPush.setRequestCnt(0);
        eventPush.setExecStatus(ExecStatusEnum.WAIT.getCode());

    }
}
