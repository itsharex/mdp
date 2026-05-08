package top.mddata.console.system.service.impl;

import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.console.system.entity.RequestLogDetail;
import top.mddata.console.system.mapper.RequestLogDetailMapper;
import top.mddata.console.system.service.RequestLogDetailService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
