package top.mddata.base.mybatisflex.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import top.mddata.base.mybatisflex.datapermission.DataPermissionAspect;

/**
 * 数据权限自动配置
 * <p>
 * 自动注册 DataPermissionAspect 切面 Bean，并启用 AspectJ 自动代理
 * </p>
 *
 * @author henhen
 * @since 2026年05月24日
 */
@AutoConfiguration
@EnableAspectJAutoProxy
public class DataPermissionAutoConfiguration {

    /**
     * 注册数据权限切面 Bean
     * <p>
     * 只有将 Aspect 注册为 Spring Bean，Spring AOP 才能激活并拦截带有 @DataPermission 注解的方法
     * </p>
     *
     * @return DataPermissionAspect 实例
     */
    @Bean
    public DataPermissionAspect dataPermissionAspect() {
        return new DataPermissionAspect();
    }
}
