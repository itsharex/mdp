package top.mddata.console.service.system.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.console.entity.system.RequestLogDetail;
import top.mddata.console.mapper.system.RequestLogDetailMapper;
import top.mddata.console.service.system.RequestLogDetailService;

/**
 * 请求日志 服务层实现。
 *
 * @author henhen6
 * @since 2026-05-08 12:35:58
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RequestLogDetailServiceImpl extends SuperServiceImpl<RequestLogDetailMapper, RequestLogDetail> implements RequestLogDetailService {

}
