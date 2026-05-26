package cn.dev33.satoken.sso;

import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Sa-Token-SSO 模块 总控类
 * 将 sa-token 官方 SaSsoManager 拆分为 client 和 server 端
 *
 * @author henhen
 * @since 2026/1/10 12:32
 */
@Slf4j
public class SaSsoServerManager {

    /**
     * Sso Server 端 配置 Bean
     */
    private static volatile SaSsoServerConfig serverConfig;

    public static SaSsoServerConfig getServerConfig() {
        if (serverConfig == null) {
            synchronized (SaSsoServerManager.class) {
                if (serverConfig == null) {
                    setServerConfig(new SaSsoServerConfig());
                }
            }
        }
        return serverConfig;
    }

    public static void setServerConfig(SaSsoServerConfig serverConfig) {
        SaSsoServerManager.serverConfig = serverConfig;
        // 如果配置了 is-check-sign=false，则打印一条警告日志
        if (!serverConfig.getIsCheckSign()) {
            printNoCheckSignWarningByStartup();
        }
    }


    // 在启动时检测到 sa-token.sso-server.is-check-sign=false 时，输出警告信息
    public static void printNoCheckSignWarningByStartup() {
        log.error("-----------------------------------------------------------------------------");
        log.error("警告信息：");
        log.error("当前配置项 sa-token.sso-server.is-check-sign=false 代表跳过 SSO 参数签名校验");
        log.error("此模式仅为方便本地调试使用，生产环境下请务必配置为 true （配置项默认为true）");
        log.error("-----------------------------------------------------------------------------");
    }

    // 在运行时检测到 sa-token.sso-server.is-check-sign=false 时，输出警告信息
    public static void printNoCheckSignWarningByRuntime() {
        log.error("警告信息：当前配置项 sa-token.sso-server.is-check-sign=false 已跳过参数签名校验，" +
                  "此模式仅为方便本地调试使用，生产环境下请务必配置为 true （配置项默认为true）");
    }
}
