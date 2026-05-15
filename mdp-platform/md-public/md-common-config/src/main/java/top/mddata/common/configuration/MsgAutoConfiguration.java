package top.mddata.common.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.datainterface.SmsReadConfig;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * 消息（站内信、短信、邮件相关）自动配置
 *
 * @author henhen
 * @date 2025年12月28日23:36:25
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(SmsReadConfig.class)
public class MsgAutoConfiguration {
    private final SmsReadConfig smsReadConfig;

    @EventListener
    public void init(ContextRefreshedEvent event) {
        SmsFactory.createSmsBlend(smsReadConfig);
        log.info("[通过配置读取接口创建全部短信实例] 加载成功");
    }

}
