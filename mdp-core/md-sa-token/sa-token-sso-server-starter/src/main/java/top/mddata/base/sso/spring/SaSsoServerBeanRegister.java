package top.mddata.base.sso.spring;

import cn.dev33.satoken.sso.SaSsoServerManager;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author henhen
 * @since 2026/1/10 18:11
 */
@ConditionalOnClass(SaSsoServerManager.class)
public class SaSsoServerBeanRegister {
    /**
     * 获取 SSO Server 端 配置对象
     * @return 配置对象
     */
    @Bean
    @ConfigurationProperties(prefix = "sa-token.sso-server")
    public SaSsoServerConfig getSaSsoServerConfig() {
        return new SaSsoServerConfig();
    }

    /**
     * 获取 SSO Server 端 SaSsoServerTemplate
     *
     * @return /
     */
    @Bean
    @ConditionalOnMissingBean(SaSsoServerTemplate.class)
    public SaSsoServerTemplate getSaSsoServerTemplate() {
        return SaSsoServerProcessor.getInstance().getSsoServerTemplate();
    }

}
