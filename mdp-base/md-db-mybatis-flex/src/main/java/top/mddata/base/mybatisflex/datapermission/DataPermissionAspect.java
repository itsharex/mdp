package top.mddata.base.mybatisflex.datapermission;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 数据权限切面
 * <p>
 * 拦截带有 @DataPermission 注解的方法，在方法执行前将注解信息存入 ThreadLocal，
 * 供 MyBatis-Flex 的 DataPermissionDialect 在生成 SQL 时使用。
 * </p>
 * <p>
 * 注意：此类必须被注册为 Spring Bean，并在配置类中添加 @EnableAspectJAutoProxy 注解
 * </p>
 *
 * @author henhen
 * @since 2026年05月24日
 */
@Slf4j
@Aspect
public class DataPermissionAspect {

    // ThreadLocal用于存储注解信息
    private static final ThreadLocal<DataPermission> THREAD_LOCAL = new ThreadLocal<>();

    @Pointcut("@annotation(dataPermission)")
    public void dataPermissionPointcut(DataPermission dataPermission) {
    }

    @Before("dataPermissionPointcut(dataPermission)")
    public void beforeMethod(DataPermission dataPermission) {
        log.debug("[数据权限] 拦截方法，设置数据权限注解到 ThreadLocal: {}", dataPermission);
        THREAD_LOCAL.set(dataPermission);
    }

    @AfterThrowing(pointcut = "dataPermissionPointcut(dataPermission)")
    public void afterThrowingMethod(DataPermission dataPermission) {
        log.debug("[数据权限] 方法抛出异常，清理 ThreadLocal");
        THREAD_LOCAL.remove();
    }

    @After("dataPermissionPointcut(dataPermission)")
    public void afterMethod(DataPermission dataPermission) {
        log.debug("[数据权限] 方法执行完毕，清理 ThreadLocal");
        THREAD_LOCAL.remove();
    }

    public static DataPermission currentDataPermission() {
        return THREAD_LOCAL.get();
    }

}
