package top.mddata;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import top.mddata.base.validator.annotation.EnableFormValidator;
import top.mddata.common.ServerApplication;

import java.net.UnknownHostException;

import static top.mddata.base.constant.Constants.UTIL_PACKAGE;


/**
 * 微服务版 控制台服务 启动类
 *
 * @author henhen6
 * @since 2026-05-10
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@EnableFormValidator
@EnableFileStorage
@EnableFeignClients(value = {
        UTIL_PACKAGE,
})
@EnableDubbo(scanBasePackages = UTIL_PACKAGE)
public class ApiServerApplication extends ServerApplication {
    public static void main(String[] args) throws UnknownHostException {
        ServerApplication.start(ApiServerApplication.class, args);
    }
}
