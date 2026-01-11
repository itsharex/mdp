package top.mddata.base.sso.spring;

import cn.dev33.satoken.sso.SaSsoClientManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 注册 Sa-Token SSO 客户端 所需要的 Bean
 * @author henhen
 * @since 2026/1/10 18:11
 */
@ConditionalOnClass(SaSsoClientManager.class)
public class SaSsoClientBeanRegister {
    /**
     * 获取 SSO Client 端 配置对象
     * @return 配置对象
     */
    @Bean("saSsoClientConfig")
    @ConfigurationProperties(prefix = "sa-token.sso-client")
    public SaSsoClientConfig getSaSsoClientConfig() {
        return new SaSsoClientConfig();
    }

    /**
     * 获取 SSO Client 端 配置对象
     * @return 配置对象
     */
    @Bean("ssoClientsConfigMap")
    @ConfigurationProperties(prefix = "sa-token.sso-clients")
    @Primary // 标记为首选，避免 Map 注入歧义
    public Map<String, SaSsoClientConfig> getSaSsoClientConfigMap() {
        return new LinkedHashMap<>(); // 用LinkedHashMap保持配置顺序
    }


    /**
     * 获取 SSO Client 端 SaSsoClientTemplate
     *
     * @return /
     */
    @Bean
    @ConditionalOnMissingBean(SaSsoClientTemplate.class)
    public SaSsoClientTemplate getSaSsoClientTemplate() {
        return SaSsoClientProcessor.getInstance().getSsoClientTemplate();
    }
}
