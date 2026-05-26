package top.mddata.console.facade.impl.system;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.console.dto.system.RequestLogDto;
import top.mddata.console.facade.system.RequestLogFacade;
import top.mddata.console.service.system.RequestLogService;

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
