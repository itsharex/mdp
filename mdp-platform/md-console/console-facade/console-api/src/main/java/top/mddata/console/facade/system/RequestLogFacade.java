package top.mddata.console.facade.system;

import top.mddata.console.dto.system.RequestLogDto;

/**
 * 操作日志
 * @author henhen
 * @since 2026/5/8 14:20
 */
public interface RequestLogFacade {
    void saveRequestLog(RequestLogDto dto);
}
