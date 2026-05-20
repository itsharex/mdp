package top.mddata.gateway.sop.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import top.mddata.open.entity.admin.ApiCallLog;
import top.mddata.open.admin.mapper.ApiCallLogMapper;

/**
 * 调用日志事件监听
 * @author henhen
 * @since 2026/1/12 23:29
 */
@Component
@RequiredArgsConstructor
public class CallLogListener {
    private final ApiCallLogMapper apiCallLogMapper;

    @EventListener({CallLogEvent.class})
    public void saveApiCallLog(CallLogEvent event) {
        ApiCallLog source = (ApiCallLog) event.getSource();
        apiCallLogMapper.insert(source);
    }
}
