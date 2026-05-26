package top.mddata.console.service.message.event;

import org.springframework.context.ApplicationEvent;
import top.mddata.console.service.message.event.dto.MsgSendEventDto;

/**
 * 消息发送事件
 *
 * @author henhen
 * @date 2020年03月18日17:22:55
 */
public class MsgSendEvent extends ApplicationEvent {
    public MsgSendEvent(MsgSendEventDto dto) {
        super(dto);
    }
}
