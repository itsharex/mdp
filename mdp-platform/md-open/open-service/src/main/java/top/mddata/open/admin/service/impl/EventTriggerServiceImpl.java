package top.mddata.open.admin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.open.admin.entity.EventTrigger;
import top.mddata.open.admin.mapper.EventTriggerMapper;
import top.mddata.open.admin.service.EventTriggerService;

/**
 * 事件触发 服务层实现。
 *
 * @author henhen6
 * @since 2026-01-12 21:29:13
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventTriggerServiceImpl extends SuperServiceImpl<EventTriggerMapper, EventTrigger> implements EventTriggerService {

}
