package top.mddata.open.admin.service;

import top.mddata.base.mvcflex.service.SuperService;
import top.mddata.open.admin.dto.EventTriggerDto;
import top.mddata.open.admin.entity.EventTrigger;

/**
 * 事件触发 服务层。
 *
 * @author henhen6
 * @since 2026-01-12 21:29:13
 */
public interface EventTriggerService extends SuperService<EventTrigger> {
    EventTrigger save(EventTriggerDto save);
}
