package top.mddata.console.service.system.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.console.entity.system.LoginLog;
import top.mddata.console.mapper.system.MdcLoginLogMapper;
import top.mddata.console.service.system.MdcLoginLogService;

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
