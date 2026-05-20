package top.mddata.open.admin.service;

import top.mddata.base.mvcflex.service.SuperService;
import top.mddata.open.entity.admin.EventPush;

import java.time.LocalDateTime;

/**
 * 事件推送任务 服务层。
 *
 * @author henhen6
 * @since 2026-01-12 21:28:36
 */
public interface EventPushService extends SuperService<EventPush> {
    /**
     * 保存事件触发 任务推送
     *
     * @param eventCode 事件编码
     * @param triggerId 触发id
     * @param requestData 事件参数
     */
    void saveByEventTrigger(String eventCode, Long triggerId, String requestData);

    /**
     * 立即执行 事件推送
     *
     * @param id 事件推送ID
     * @return 返回结果
     */
    Long executeImmediately(Long id);

    /**
     * 事件重试
     * @param now 当前时间
     */
    void retry(LocalDateTime now);

    /**
     * 结束重试
     * @param id 任务id
     * @return 是否成功
     */
    Boolean end(Long id);
}
