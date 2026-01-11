package cn.dev33.satoken.sso;

import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Sa-Token-SSO 模块 总控类
 * 将 sa-token 官方 SaSsoManager 拆分为 client 和 server 端
 *
 * @author henhen
 * @since 2026/1/10 12:32
 */
@Slf4j
public class SaSsoClientManager {

    /**
     * Sso Client 端 配置 Bean
     */
    private static volatile SaSsoClientConfig clientConfig;
    private static volatile Map<String, SaSsoClientConfig> clientConfigMap;

    public static SaSsoClientConfig getClientConfig() {
        if (clientConfig == null) {
            synchronized (SaSsoClientManager.class) {
                if (clientConfig == null) {
                    setClientConfig(new SaSsoClientConfig());
                }
            }
        }
        return clientConfig;
    }

    public static void setClientConfig(SaSsoClientConfig clientConfig) {
        SaSsoClientManager.clientConfig = clientConfig;
        // 如果配置了 is-check-sign=false，则打印一条警告日志
        if (!clientConfig.getIsCheckSign()) {
            printNoCheckSignWarningByStartup();
        }
    }

    public static Map<String, SaSsoClientConfig> getClientConfigMap() {
        if (clientConfigMap == null) {
            synchronized (SaSsoClientManager.class) {
                if (clientConfigMap == null) {
                    setClientConfigMap(new LinkedHashMap<>());
                }
            }
        }
        return clientConfigMap;
    }

    public static void setClientConfigMap(Map<String, SaSsoClientConfig> clientConfigMap) {
        SaSsoClientManager.clientConfigMap = clientConfigMap;
        // 如果配置了 is-check-sign=false，则打印一条警告日志
        if (CollUtil.isNotEmpty(clientConfigMap)) {
            clientConfigMap.forEach((key, value) -> {
                if (!value.getIsCheckSign()) {
                    printNoCheckSignWarningByStartup(key);
                }
            });
        }
    }


    // 在启动时检测到 sa-token.sso-client.is-check-sign=false 时，输出警告信息
    public static void printNoCheckSignWarningByStartup() {
        log.error("""
                -----------------------------------------------------------------------------
                警告信息：
                当前配置项 sa-token.sso-client.is-check-sign=false 代表跳过 SSO 参数签名校验
                此模式仅为方便本地调试使用，生产环境下请务必配置为 true （配置项默认为true）
                -----------------------------------------------------------------------------
                """);
    }

    public static void printNoCheckSignWarningByStartup(String clientId) {
        log.error("""
                -----------------------------------------------------------------------------
                警告信息：
                当前配置项 sa-token.sso-clients.{}.is-check-sign=false 代表跳过 SSO 参数签名校验
                此模式仅为方便本地调试使用，生产环境下请务必配置为 true （配置项默认为true）
                -----------------------------------------------------------------------------
                """, clientId);
    }

    // 在运行时检测到 sa-token.sso-client.is-check-sign=false 时，输出警告信息
    public static void printNoCheckSignWarningByRuntime() {
        log.error("""
                警告信息：当前配置项 sa-token.sso-client.is-check-sign=false 已跳过参数签名校验，
                此模式仅为方便本地调试使用，生产环境下请务必配置为 true （配置项默认为true）
                """);
    }
}
