package top.mddata.base.mybatisflex.datapermission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.Set;

/**
 * 数据权限处理器实现类
 *
 * @author <a href="https://mybatis-flex.com/zh/core/data-permission.html">数据权限</a>
 * @author henhen
 * @since 2026年05月24日
 */
public class DataPermissionDialect extends CommonsDialectImpl {

    private final DataPermissionFilter dataPermissionFilter;

    public DataPermissionDialect(DataPermissionFilter dataPermissionFilter) {
        this.dataPermissionFilter = dataPermissionFilter;
    }

    @Override
    public String forSelectByQuery(QueryWrapper queryWrapper) {
        if (!dataPermissionFilter.isFilter()) {
            return super.buildSelectSql(queryWrapper);
        }
        DataPermission dataPermission = DataPermissionAspect.currentDataPermission();
        if (dataPermission == null) {
            return super.buildSelectSql(queryWrapper);
        }
        DataPermissionCurrentUser currentUser = dataPermissionFilter.getCurrentUser();
        Set<DataPermissionCurrentUser.CurrentUserRole> roles = currentUser.getRoles();
        if (CollUtil.isEmpty(roles)) {
            return super.buildSelectSql(queryWrapper);
        }
        for (DataPermissionCurrentUser.CurrentUserRole role : roles) {
            DataScope dataScope = role.getDataScope();
            if (DataScope.ALL.equals(dataScope)) {
                return super.buildSelectSql(queryWrapper);
            }
            switch (dataScope) {
                case DEPT_AND_CHILD -> this.buildDeptAndChildExpression(dataPermission, currentUser, queryWrapper);
                case DEPT -> this.buildDeptExpression(dataPermission, currentUser, queryWrapper);
                case SELF -> this.buildSelfExpression(dataPermission, currentUser, queryWrapper);
                case CUSTOM -> this.buildCustomExpression(dataPermission, role, queryWrapper);
                default -> throw new IllegalArgumentException("暂不支持 [%s] 数据权限".formatted(dataScope));
            }
        }
        return super.buildSelectSql(queryWrapper);
    }

    /**
     * 构建自定义数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.dept_id in (select dept_id from sys_role_dept where
     * role_id = xxx);
     * </p>
     *
     * @param dataPermission 数据权限
     * @param role           当前用户角色
     * @param queryWrapper   查询条件
     * @return 处理完后的表达式
     */
    private void buildCustomExpression(DataPermission dataPermission,
                                       DataPermissionCurrentUser.CurrentUserRole role,
                                       QueryWrapper queryWrapper) {
        QueryWrapper subQueryWrapper = QueryWrapper.create();
        subQueryWrapper.select(dataPermission.deptId()).from(dataPermission.roleDeptTableAlias());
        subQueryWrapper.eq(dataPermission.roleId(), role.getRoleId());
        queryWrapper.in(buildColumn(dataPermission.tableAlias(), dataPermission.deptId()), subQueryWrapper);
    }

    /**
     * 构建仅本人数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.create_user = xxx;
     * </p>
     *
     * @param dataPermission 数据权限
     * @param currentUser    当前用户
     * @param queryWrapper   处理前的表达式
     */
    private void buildSelfExpression(DataPermission dataPermission,
                                     DataPermissionCurrentUser currentUser,
                                     QueryWrapper queryWrapper) {
        queryWrapper.eq(buildColumn(dataPermission.tableAlias(), dataPermission.userId()), currentUser.getUserId());
    }

    /**
     * 构建本部门数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.dept_id = xxx;
     * </p>
     *
     * @param dataPermission 数据权限
     * @param currentUser    当前用户
     * @param queryWrapper   查询条件
     */
    private void buildDeptExpression(DataPermission dataPermission,
                                     DataPermissionCurrentUser currentUser,
                                     QueryWrapper queryWrapper) {
        queryWrapper.eq(buildColumn(dataPermission.tableAlias(), dataPermission.deptId()), currentUser.getDeptId());
    }

    /**
     * 构建本部门及以下数据权限表达式
     *
     * <p>
     * 处理完后的 SQL 示例：<br /> select t1.* from table as t1 where t1.dept_id in (select id from sys_dept where id = xxx or
     * find_in_set(xxx, ancestors));
     * </p>
     *
     * @param dataPermission 数据权限
     * @param currentUser    当前用户
     * @param queryWrapper   查询条件
     */
    private void buildDeptAndChildExpression(DataPermission dataPermission,
                                             DataPermissionCurrentUser currentUser,
                                             QueryWrapper queryWrapper) {
        QueryWrapper subQueryWrapper = QueryWrapper.create();
        subQueryWrapper.select(dataPermission.id()).from(dataPermission.deptTableAlias());
        subQueryWrapper.and(qw -> {
            qw.eq(dataPermission.id(), currentUser.getDeptId())
                .or("find_in_set(" + currentUser.getDeptId() + ",ancestors)");
        });
        queryWrapper.in(buildColumn(dataPermission.tableAlias(), dataPermission.deptId()), subQueryWrapper);
    }

    /**
     * 构建 Column
     *
     * @param tableAlias 表别名
     * @param columnName 字段名称
     * @return 带表别名字段
     */
    private String buildColumn(String tableAlias, String columnName) {
        if (CharSequenceUtil.isNotEmpty(tableAlias)) {
            return "%s.%s".formatted(tableAlias, columnName);
        }
        return columnName;
    }

}
