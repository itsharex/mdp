package top.mddata.console.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.common.entity.User;
import top.mddata.console.organization.service.UserService;
import top.mddata.console.system.entity.RequestLog;
import top.mddata.console.system.entity.RequestLogDetail;
import top.mddata.console.system.mapper.RequestLogMapper;
import top.mddata.console.system.service.RequestLogDetailService;
import top.mddata.console.system.service.RequestLogService;

/**
 * 请求日志 服务层实现。
 *
 * @author henhen6
 * @since 2025-11-12 16:21:25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RequestLogServiceImpl extends SuperServiceImpl<RequestLogMapper, RequestLog> implements RequestLogService {

    private final RequestLogDetailService requestLogDetailService;
    private final UserService userService;

    @Override
    protected RequestLog saveBefore(Object save) {
        RequestLog requestLog = super.saveBefore(save);
        if (requestLog.getUserId() != null) {
            User user = userService.getById(requestLog.getUserId());
            if (user != null) {
                requestLog.setUserName(user.getName());
            }
        }
        return requestLog;
    }

    @Override
    protected void saveAfter(Object save, RequestLog entity) {
        RequestLogDetail detail = BeanUtil.toBean(save, RequestLogDetail.class);
        detail.setId(entity.getId());
        requestLogDetailService.save(detail);
    }
}
