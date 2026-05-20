package top.mddata.console.system.mapper;

import org.springframework.stereotype.Repository;
import top.mddata.base.mvcflex.mapper.SuperMapper;
import top.mddata.console.entity.system.LoginLog;

/**
 * 登录日志 映射层。
 *
 * @author henhen6
 * @since 2025-11-12 23:46:53
 */
@Repository
public interface MdcLoginLogMapper extends SuperMapper<LoginLog> {

}
