package top.mddata.console.system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.console.system.entity.LoginLog;
import top.mddata.console.system.mapper.MdcLoginLogMapper;
import top.mddata.console.system.service.MdcLoginLogService;

/**
 * 登录日志 服务层实现。
 *
 * @author henhen6
 * @since 2025-11-12 23:46:53
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MdcLoginLogServiceImpl extends SuperServiceImpl<MdcLoginLogMapper, LoginLog> implements MdcLoginLogService {
}
