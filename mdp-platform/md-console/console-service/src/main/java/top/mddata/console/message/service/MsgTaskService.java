package top.mddata.console.message.service;

import top.mddata.base.mvcflex.service.SuperService;
import top.mddata.console.dto.message.MsgSendDto;
import top.mddata.console.dto.message.MsgTaskDto;
import top.mddata.console.entity.message.MsgTask;

/**
 * 消息任务 服务层。
 *
 * @author henhen6
 * @since 2025-12-21 00:02:22
 */
public interface MsgTaskService extends SuperService<MsgTask> {

    /**
     * 发布站内信
     * @param data 站内信
     * @return 是否成功
     */
    Boolean publish(MsgTaskDto data);

    /**
     * 根据消息模板发送消息
     *
     * @param data 消息参数
     */
    Long sendByTemplateKey(MsgSendDto data);
}
