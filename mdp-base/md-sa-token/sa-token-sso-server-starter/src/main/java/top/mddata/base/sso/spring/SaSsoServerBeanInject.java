package top.mddata.base.sso.spring;

import cn.dev33.satoken.sso.SaSsoServerManager;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

/**
 * 注入 Sa-Token SSO 服务端 所需要的 Bean
 * @author henhen
 * @since 2026/1/10 18:12
 */
@ConditionalOnClass(SaSsoServerManager.class)
public class SaSsoServerBeanInject {
    /**
     * 注入 Sa-Token SSO Server 端 配置类
     *
     * @param serverConfig 配置对象
     */
    @Autowired(required = false)
    public void setSaSsoServerConfig(SaSsoServerConfig serverConfig) {
        SaSsoServerManager.setServerConfig(serverConfig);
    }

    /**
     * 注入 SSO 模板代码类 (Server 端)
     *
     * @param ssoServerTemplate /
     */
    @Autowired(required = false)
    public void setSaSsoServerTemplate(SaSsoServerTemplate ssoServerTemplate) {
        SaSsoServerProcessor.getInstance().setSsoServerTemplate(ssoServerTemplate);
    }

}
