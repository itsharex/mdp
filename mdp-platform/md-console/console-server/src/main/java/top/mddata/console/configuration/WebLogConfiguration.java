package top.mddata.console.configuration;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mddata.base.constant.Constants;
import top.mddata.base.log.event.SysLogListener;
import top.mddata.console.dto.system.RequestLogDto;
import top.mddata.console.system.service.RequestLogService;

/**
 *
 * @author henhen
 * @since 2026/5/8 14:31
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebLogConfiguration {
    /**
     * 操作日志 监听器
     */
    @Bean
    @ConditionalOnExpression("${" + Constants.PROJECT_PREFIX + ".log.enabled:true} && 'DB'.equals('${" + Constants.PROJECT_PREFIX + ".log.type:LOGGER}')")
    public SysLogListener sysLogListener(RequestLogService requestLogService) {
        return new SysLogListener(data -> {
            RequestLogDto dto = BeanUtil.toBean(data, RequestLogDto.class);
            dto.setLogType(data.getLogType().getValue());
            requestLogService.saveDto(dto);
        });
    }
}
