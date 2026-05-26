package top.mddata.base.sso.spring;

import cn.dev33.satoken.sso.SaSsoClientManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.util.Map;


/**
 * 注入 Sa-Token SSO 客户端 所需要的 Bean
 * @author henhen
 * @since 2026/1/10 18:12
 */
@ConditionalOnClass(SaSsoClientManager.class)
public class SaSsoClientBeanInject {

    // 1. 注入默认单客户端配置（显式指定 Bean 名，避免歧义）
    @Autowired(required = false)
    @Qualifier("saSsoClientConfig")
    private SaSsoClientConfig defaultSaSsoClientConfig;

    // 2. 注入配置绑定的多客户端 Map（关键：@Qualifier 指定命名的 Map Bean）
    @Autowired(required = false)
    @Qualifier("ssoClientsConfigMap") // 明确注入配置绑定的 Map，而非 Bean 收集的 Map
    private Map<String, SaSsoClientConfig> ssoClientsConfigMap;


    /**
     * Bean初始化完成后统一设置配置（执行顺序完全可控）
     */
    @PostConstruct
    public void initSsoClientConfig() {
        // 第一步：设置默认单客户端配置
        if (defaultSaSsoClientConfig != null) {
            SaSsoClientManager.setClientConfig(defaultSaSsoClientConfig);
        }

        // 第二步：设置多客户端配置Map（此时Map已完全初始化）
        if (ssoClientsConfigMap != null && !ssoClientsConfigMap.isEmpty()) {
            SaSsoClientManager.setClientConfigMap(ssoClientsConfigMap);
        }
    }

    /**
     * 注入 SSO 模板代码类 (Client 端)
     *
     * @param ssoClientTemplate /
     */
    @Autowired(required = false)
    public void setSaSsoClientTemplate(SaSsoClientTemplate ssoClientTemplate) {
        SaSsoClientProcessor.getInstance().setSsoClientTemplate(ssoClientTemplate);
    }
}
