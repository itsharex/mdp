package top.mddata.console.system.facade;

import top.mddata.console.system.dto.RequestLogDto;

/**
 * 操作日志
 * @author henhen
 * @since 2026/5/8 14:20
 */
public interface RequestLogFacade {
    void saveRequestLog(RequestLogDto dto);
}
