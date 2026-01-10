package top.mddata.base.sso.spring;

import cn.dev33.satoken.sso.SaSsoClientManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;


/**
 * 注入 Sa-Token SSO 客户端 所需要的 Bean
 * @author henhen
 * @since 2026/1/10 18:12
 */
@ConditionalOnClass(SaSsoClientManager.class)
public class SaSsoClientBeanInject {

    /**
     * 注入 Sa-Token SSO Client 端 配置类
     *
     * @param clientConfig 配置对象
     */
    @Autowired(required = false)
    public void setSaSsoClientConfig(SaSsoClientConfig clientConfig) {
        SaSsoClientManager.setClientConfig(clientConfig);
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
