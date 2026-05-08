package top.mddata.console.system.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.console.system.dto.RequestLogDto;
import top.mddata.console.system.facade.RequestLogFacade;
import top.mddata.console.system.service.RequestLogService;

/**
 *
 * @author henhen
 * @since 2026/5/8 14:25
 */
@Service
@RequiredArgsConstructor
public class RequestLogFacadeImpl implements RequestLogFacade {
    private final RequestLogService requestLogService;

    @Override
    public void saveRequestLog(RequestLogDto dto) {
        requestLogService.saveDto(dto);
    }
}
