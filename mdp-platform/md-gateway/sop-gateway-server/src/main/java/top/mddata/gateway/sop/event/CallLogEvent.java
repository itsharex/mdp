package top.mddata.gateway.sop.event;

import org.springframework.context.ApplicationEvent;
import top.mddata.open.admin.entity.ApiCallLog;

/**
 * 调用日志
 *
 * @author henhen6
 * @date 2026年01月12日23:29:21
 */
public class CallLogEvent extends ApplicationEvent {
    public CallLogEvent(ApiCallLog source) {
        super(source);
    }
}
