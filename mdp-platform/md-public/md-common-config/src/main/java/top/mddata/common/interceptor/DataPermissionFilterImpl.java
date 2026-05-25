package top.mddata.common.interceptor;

import org.springframework.stereotype.Component;
import top.mddata.base.mybatisflex.datapermission.DataPermissionCurrentUser;
import top.mddata.base.mybatisflex.datapermission.DataPermissionFilter;
import top.mddata.base.util.ContextUtil;

/**
 * 数据权限数据实现类
 *
 * @author tangyh
 * @since 2026/5/24 23:38
 */
@Component
public class DataPermissionFilterImpl implements DataPermissionFilter {
    @Override
    public boolean isFilter() {
        return true;
    }

    @Override
    public DataPermissionCurrentUser getCurrentUser() {
        DataPermissionCurrentUser dataPermissionCurrentUser = new DataPermissionCurrentUser();
        dataPermissionCurrentUser.setUserId(ContextUtil.getUserId());
        dataPermissionCurrentUser.setDeptId(ContextUtil.getCurrentDeptId());
        return dataPermissionCurrentUser;
    }
}
