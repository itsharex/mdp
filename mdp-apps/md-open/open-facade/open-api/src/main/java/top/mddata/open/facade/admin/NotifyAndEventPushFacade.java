package top.mddata.open.facade.admin;

import top.mddata.base.base.R;
import top.mddata.open.dto.admin.EventTriggerDto;
import top.mddata.open.dto.admin.NotifyInfoDto;
import top.mddata.open.entity.admin.EventTrigger;

/**
 * 回调任务 接口
 * @author henhen
 * @since 2026/1/13 00:05
 */
public interface NotifyAndEventPushFacade {
    /**
     * 接口调用回调
     *
     * @param request 回调内容
     * @return 返回回调id
     */
    R<Long> apiCallNotify(NotifyInfoDto request);

    /**
     * 事件推送任务
     *
     * @param request 事件触发参数
     * @return 推送id
     */
    R<EventTrigger> eventPush(EventTriggerDto request);
}
