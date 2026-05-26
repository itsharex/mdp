package top.mddata.base.mybatisflex.datapermission;

import java.util.Set;

/**
 * 当前用户信息
 *
 * @author henhen
 * @since 2026年05月24日
 */
public class DataPermissionCurrentUser {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 角色列表
     */
    private Set<CurrentUserRole> roles;

    /**
     * 部门 ID
     */
    private Long deptId;

    /**
     * 当前用户角色信息
     */
    public static class CurrentUserRole {

        /**
         * 角色 ID
         */
        private Long roleId;

        /**
         * 数据权限
         */
        private DataScope dataScope;

        public CurrentUserRole() {
        }

        public CurrentUserRole(Long roleId, DataScope dataScope) {
            this.roleId = roleId;
            this.dataScope = dataScope;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public DataScope getDataScope() {
            return dataScope;
        }

        public void setDataScope(DataScope dataScope) {
            this.dataScope = dataScope;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<CurrentUserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<CurrentUserRole> roles) {
        this.roles = roles;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
