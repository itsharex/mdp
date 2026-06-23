/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.sso.processor;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.sso.SaSsoServerManager;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import cn.dev33.satoken.util.SaSugar;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * SSO 请求处理器 （Server端）
 *
 * @author click33
 * @since 1.38.0
 */

public class SaSsoServerProcessor {

    /**
     * 全局默认实例
     */
    @Getter
    @Setter
    private static SaSsoServerProcessor instance = new SaSsoServerProcessor();

    /**
     * 底层 SaSsoServerTemplate 对象
     */
    @Getter
    @Setter
    private SaSsoServerTemplate ssoServerTemplate = new SaSsoServerTemplate();

    // ----------- SSO-Server 端路由分发 -----------

    /**
     * 分发 Server 端所有请求
     *
     * @return 处理结果
     */
    public Object dister() {

        // 获取对象
        SaRequest req = SaHolder.getRequest();
        ApiName apiName = ssoServerTemplate.getApiName();

        // ------------------ 路由分发 ------------------

        // sso-server：授权地址
        if (req.isPath(apiName.getSsoAuth())) {
            return ssoAuth();
        }

        // sso-server：RestAPI 登录接口
        if (req.isPath(apiName.getSsoDoLogin())) {
            return ssoDoLogin();
        }

        // sso-server：单点注销
        if (req.isPath(apiName.getSsoSignout())) {
            return ssoSignout();
        }

        // sso-server：接收推送消息
        if (req.isPath(apiName.getSsoPushS())) {
            return ssoPushS();
        }

        // 默认返回
        return SaSsoConsts.NOT_HANDLE;
    }

    /**
     * SSO-Server端：授权地址
     * @return 处理结果
     */
    public Object ssoAuth() {
        // 获取对象
        SaRequest req = SaHolder.getRequest();
        SaResponse res = SaHolder.getResponse();
        SaSsoServerConfig cfg = ssoServerTemplate.getServerConfig();
        StpLogic stpLogic = ssoServerTemplate.getStpLogicOrGlobal();
        ParamName paramName = ssoServerTemplate.getParamName();

        // 两种情况：
        // 		情况1：在 SSO 认证中心尚未登录，需要显示登录视图，去登录
        // 		情况2：在 SSO 认证中心已经登录，需要重定向回 Client 端

        // 情况1，显示登录视图
        if (!stpLogic.isLogin()) {
            return ssoServerTemplate.getStrategy().getNotLoginView().get();
        }

        // 情况2，开始跳转
        String mode = req.getParam(paramName.getMode(), SaSsoConsts.MODE_TICKET);
        String redirect = req.getParam(paramName.getRedirect());
        String client = req.getParam(paramName.getClient());

        // 构建最终重定向地址
        String redirectUrl = SaSugar.get(() -> {

            // 若 redirect 参数为空，说明用户并不是从 client 重定向来的，而是直接访问的 http://sso-server.com/sso/auth 地址
            // 此时需要跳转到配置的 homeRoute 路由上，
            // 若 homeRoute 也为空，则没有明确的跳转地址了，需要抛出异常
            if (SaFoxUtil.isEmpty(redirect)) {
                if (SaFoxUtil.isEmpty(cfg.getHomeRoute())) {
                    throw new SaSsoException("未指定 redirect 参数，也未配置 homeRoute 路由，无法完成重定向操作").setCode(SaSsoErrorCode.CODE_30014);
                }
                return cfg.getHomeRoute();
            }

            // 方式1：直接重定向回Client端 (mode=simple，一般是模式一)
            if (mode.equals(SaSsoConsts.MODE_SIMPLE)) {
                ssoServerTemplate.checkRedirectUrl(client, redirect);
                return redirect;
            } else {
                // 方式2：带着 ticket 参数重定向回Client端 (mode=ticket，一般是模式二、三)

                // 构建并跳转
                String tempRedirectUrl = ssoServerTemplate.buildRedirectUrl(client, redirect, stpLogic.getLoginId(), stpLogic.getTokenValue());

                // 构建成功，说明 redirect 地址合法，此时需要更新一下当前 token 有效期
                if (cfg.getAutoRenewTimeout()) {
                    stpLogic.renewTimeout(stpLogic.getConfigOrGlobal().getTimeout());
                }
                return tempRedirectUrl;
            }
        });

        // 跳转
        ssoServerTemplate.getStrategy().getJumpToRedirectUrlNotice().run(redirectUrl);
        return res.redirect(redirectUrl);
    }

    /**
     * SSO-Server端：RestAPI 登录接口
     * @return 处理结果
     */
    public Object ssoDoLogin() {
        // 获取参数
        SaRequest req = SaHolder.getRequest();
        ParamName paramName = ssoServerTemplate.getParamName();
        String name = req.getParam(paramName.getName());
        String pwd = req.getParam(paramName.getPwd());

        // 处理
        return ssoServerTemplate.getStrategy().getDoLoginHandle().apply(name, pwd);
    }

    /**
     * SSO-Server端：单点注销
     * @return 处理结果
     */
    public Object ssoSignout() {
        // 获取对象
        SaRequest req = SaHolder.getRequest();
        SaResponse res = SaHolder.getResponse();
        StpLogic stpLogic = ssoServerTemplate.getStpLogicOrGlobal();
        Object loginId = stpLogic.getLoginIdDefaultNull();
        boolean singleDeviceIdLogout = req.isParam(ssoServerTemplate.getParamName().getSingleDeviceIdLogout(), "true");

        // 单点注销
        if (SaFoxUtil.isNotEmpty(loginId)) {
            SaLogoutParameter logoutParameter = stpLogic.createSaLogoutParameter();
            if (singleDeviceIdLogout) {
                logoutParameter.setDeviceId(stpLogic.getLoginDeviceId());
            }
            ssoServerTemplate.ssoLogout(loginId, logoutParameter, null);
        }

        // 完成
        return _ssoLogoutBack(req, res);
    }

    /**
     * SSO-Server端：接收推送消息
     *
     * @return 处理结果
     */
    public Object ssoPushS() {
        ParamName paramName = ssoServerTemplate.getParamName();
        SaSsoServerConfig ssoServerConfig = ssoServerTemplate.getServerConfig();

        // 1、获取参数
        SaRequest req = SaHolder.getRequest();
        String client = req.getParam(paramName.getClient());

        // 2、校验提供的client是否为非法字符
        if (SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
            return SaResult.error("无效 client 标识：" + client);
        }

        // 3、校验参数签名
        Map<String, String> paramMap = req.getParamMap();
        if (ssoServerConfig.getIsCheckSign()) {
            ssoServerTemplate.getSignTemplate(client).checkParamMap(paramMap);
        } else {
            SaSsoServerManager.printNoCheckSignWarningByRuntime();
        }

        // 4、处理消息
        SaSsoMessage message = new SaSsoMessage(paramMap);
        if (!ssoServerTemplate.getMessageHolder().hasHandle(message.getType())) {
            return SaResult.error("未能找到消息处理器：" + message.getType());
        }
        return ssoServerTemplate.handleMessage(message);
    }

    /**
     * 封装：单点注销成功后返回结果
     * @param req SaRequest对象
     * @param res SaResponse对象
     * @return 返回结果
     */
    public Object _ssoLogoutBack(SaRequest req, SaResponse res) {
        return SaSsoProcessorHelper.ssoLogoutBack(req, res, ssoServerTemplate.getParamName());
    }

    /**
     * 获取当前请求中的 client 参数
     */
    public String getClient() {
        ParamName paramName = ssoServerTemplate.getParamName();

        // 1、获取参数
        SaRequest req = SaHolder.getRequest();
        return req.getParam(paramName.getClient());
    }
}
