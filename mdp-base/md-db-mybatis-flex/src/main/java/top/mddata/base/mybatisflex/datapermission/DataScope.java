package top.mddata.base.mybatisflex.datapermission;

/**
 * 数据权限枚举
 *
 * @author henhen
 * @since 2026年05月24日
 */
public enum DataScope {

    /**
     * 全部数据权限
     */
    ALL,

    /**
     * 本部门及以下数据权限
     */
    DEPT_AND_CHILD,

    /**
     * 本部门数据权限
     */
    DEPT,

    /**
     * 仅本人数据权限
     */
    SELF,

    /**
     * 自定义数据权限
     */
    CUSTOM,
}
