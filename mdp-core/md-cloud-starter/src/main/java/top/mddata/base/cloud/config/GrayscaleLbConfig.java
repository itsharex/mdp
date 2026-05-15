package top.mddata.base.cloud.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import top.mddata.base.cloud.rule.GrayscaleVersionRoundRobinLoadBalancer;
import top.mddata.base.util.StrPool;

/**
 * 灰度配置
 *
 * @author henhen
 * @version v1.0
 */
public class GrayscaleLbConfig {

    @Bean
    @Primary
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment,
                                                                                   LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME, StrPool.EMPTY);
        return new GrayscaleVersionRoundRobinLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}
