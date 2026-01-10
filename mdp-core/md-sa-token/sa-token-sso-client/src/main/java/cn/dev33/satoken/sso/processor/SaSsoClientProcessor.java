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
import cn.dev33.satoken.sso.SaSsoClientManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.model.TicketModel;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * SSO 请求处理器 （Client端）
 *
 * @author click33
 * @since 1.38.0
 */
@Slf4j
public class SaSsoClientProcessor {

    /**
     * 全局默认实例
     */
    @Getter
    @Setter
    private static SaSsoClientProcessor instance = new SaSsoClientProcessor();

    /**
     * 底层 SaSsoClientTemplate 对象
     */
    @Getter
    @Setter
    private SaSsoClientTemplate ssoClientTemplate = new SaSsoClientTemplate();

    // ----------- SSO-Client 端路由分发 -----------

    /**
     * 分发 Client 端所有请求
     * @return 处理结果
     */
    public Object dister() {
        ApiName apiName = ssoClientTemplate.getApiName();

        // 获取对象
        SaRequest req = SaHolder.getRequest();
        SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();

        // ------------------ 路由分发 ------------------

        // sso-client：登录地址
        if (req.isPath(apiName.getSsoLogin())) {
            return ssoLogin();
        }

        // sso-client：单点注销
        if (req.isPath(apiName.getSsoLogout())) {
            return ssoLogout();
        }

        // sso-client：接收消息推送
        if (req.isPath(apiName.getSsoPushC())) {
            return ssoPushC();
        }

        // sso-client：单点注销的回调
        if (req.isPath(apiName.getSsoLogoutCall()) && cfg.getRegLogoutCall()) {
            return ssoLogoutCall();
        }

        // 默认返回
        return SaSsoConsts.NOT_HANDLE;
    }

    /**
     * SSO-Client端：登录地址
     * @return 处理结果
     */
    public Object ssoLogin() {
        // 获取对象
        SaRequest req = SaHolder.getRequest();
        ParamName paramName = ssoClientTemplate.getParamName();
        String ticket = req.getParam(paramName.getTicket());

        /*
         * 此时有两种情况:
         * 		情况1：ticket 无值，说明此请求是 sso-client 端访问，需要重定向至 sso-server 认证中心
         * 		情况2：ticket 有值，说明此请求从 sso-server 认证中心重定向而来，需要根据 ticket 进行登录
         */
        if (ticket == null) {
            return _goServerAuth();
        } else {
            return _loginByTicket();
        }
    }

    /**
     * SSO-Client端：单点注销
     * @return 处理结果
     */
    public Object ssoLogout() {
        // 获取对象
        SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();

        // 无论登录时选择的是模式二还是模式三
        // 		在注销时都应该按照模式三的方法，通过 http 请求调用 sso-server 的单点注销接口来做到全端下线
        //		如果按照模式二的方法注销，则会导致按照模式三登录的应用无法参与到单点注销环路中来
        if (cfg.getIsSlo()) {
            return _ssoLogoutByMode3();
        }

        // 默认返回
        return SaSsoConsts.NOT_HANDLE;
    }

    /**
     * SSO-Client端：接收推送消息
     *
     * @return 处理结果
     */
    public Object ssoPushC() {
        SaSsoClientConfig ssoClientConfig = ssoClientTemplate.getClientConfig();

        // 1、校验签名
        Map<String, String> paramMap = SaHolder.getRequest().getParamMap();
        if (ssoClientConfig.getIsCheckSign()) {
            ssoClientTemplate.getSignTemplate().checkParamMap(paramMap);
        } else {
            SaSsoClientManager.printNoCheckSignWarningByRuntime();
        }

        // 2、处理消息
        SaSsoMessage message = new SaSsoMessage(paramMap);
        return ssoClientTemplate.handleMessage(message);
    }

    /**
     * SSO-Client端：单点注销的回调 [模式三]
     * @return 处理结果
     */
    public Object ssoLogoutCall() {

        // 获取对象
        SaRequest req = SaHolder.getRequest();
        StpLogic stpLogic = ssoClientTemplate.getStpLogicOrGlobal();
        ParamName paramName = ssoClientTemplate.getParamName();
        SaSsoClientConfig ssoConfig = ssoClientTemplate.getClientConfig();

        // 获取参数
        Object loginId = req.getParamNotNull(paramName.getLoginId());
        loginId = ssoClientTemplate.getStrategy().getConvertCenterIdToLoginId().run(loginId);
        String deviceId = req.getParam(paramName.getDeviceId());

        // 校验参数签名
        if (ssoConfig.getIsCheckSign()) {
            ssoClientTemplate.getSignTemplate().checkRequest(req);
        } else {
            SaSsoClientManager.printNoCheckSignWarningByRuntime();
        }

        // 注销当前应用端会话
        SaLogoutParameter logoutParameter = ssoClientTemplate.getStpLogicOrGlobal().createSaLogoutParameter();
        stpLogic.logout(loginId, logoutParameter.setDeviceId(deviceId));

        // 响应
        return SaResult.ok("单点注销回调成功");
    }

    // 次级方法

    /**
     * 跳转去 sso-server 认证中心
     * @return /
     */
    public Object _goServerAuth() {
        // 获取对象
        SaRequest req = SaHolder.getRequest();
        SaResponse res = SaHolder.getResponse();
        SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();
        StpLogic stpLogic = ssoClientTemplate.getStpLogicOrGlobal();
        ParamName paramName = ssoClientTemplate.getParamName();

        // 获取参数
        String back = req.getParam(paramName.getBack(), "/");

        // 如果当前 sso-client 端已经登录，则无需访问 SSO 认证中心，可以直接返回
        if (stpLogic.isLogin()) {
            return res.redirect(back);
        }

        // 获取当前项目的 sso 登录中转页地址，形如：http://sso-client.com/sso/login
        // 		全局配置了就是用全局的，否则使用当前请求的地址
        String currSsoLoginUrl = cfg.getCurrSsoLogin();
        if (SaFoxUtil.isEmpty(currSsoLoginUrl)) {
            currSsoLoginUrl = SaHolder.getRequest().getUrl();
        }
        // 构建最终授权地址 url，形如：http://sso-server.com/sso/auth?redirectUrl=http://sso-client.com/sso/login?back=http://sso-client.com
        String serverAuthUrl = ssoClientTemplate.buildServerAuthUrl(currSsoLoginUrl, back);
        return res.redirect(serverAuthUrl);
    }

    /**
     * 根据认证中心回传的 ticket 进行登录
     * @return /
     */
    public Object _loginByTicket() {
        // 获取对象
        SaRequest req = SaHolder.getRequest();
        SaResponse res = SaHolder.getResponse();
        StpLogic stpLogic = ssoClientTemplate.getStpLogicOrGlobal();
        ParamName paramName = ssoClientTemplate.getParamName();
        ApiName apiName = ssoClientTemplate.getApiName();

        // 获取参数
        String back = req.getParam(paramName.getBack(), "/");
        String ticket = req.getParam(paramName.getTicket());

        // 1、校验 ticket，获取 loginId 等数据
        SaCheckTicketResult ctr = checkTicket(ticket, apiName.getSsoLogin());

        // 2、如果开发者自定义了 ticket 结果值处理函数，则使用自定义的函数
        if (ssoClientTemplate.getStrategy().getTicketResultHandle() != null) {
            return ssoClientTemplate.getStrategy().getTicketResultHandle().run(ctr, back);
        }

        // 3、登录并重定向至back地址
        stpLogic.login(ctr.getLoginId(), new SaLoginParameter()
                .setTimeout(ctr.getRemainTokenTimeout())
                .setDeviceId(ctr.getDeviceId())
        );
        return res.redirect(back);
    }

    /**
     * SSO-Client端：单点注销 [模式三]
     * @return 处理结果
     */
    public Object _ssoLogoutByMode3() {
        // 获取对象
        SaRequest req = SaHolder.getRequest();
        SaResponse res = SaHolder.getResponse();
        StpLogic stpLogic = ssoClientTemplate.getStpLogicOrGlobal();
        boolean singleDeviceIdLogout = req.isParam(ssoClientTemplate.getParamName().getSingleDeviceIdLogout(), "true");

        // 如果未登录，则无需注销
        if (!stpLogic.isLogin()) {
            return _ssoLogoutBack(req, res);
        }

        // 向 sso-server 认证中心推送消息：单点注销
        SaLogoutParameter logoutParameter = stpLogic.createSaLogoutParameter();
        if (singleDeviceIdLogout) {
            logoutParameter.setDeviceId(stpLogic.getLoginDeviceId());
        }
        Object loginId = stpLogic.getLoginId();
        Object centerId = ssoClientTemplate.getStrategy().getConvertCenterIdToLoginId().run(loginId);
        SaSsoMessage message = ssoClientTemplate.buildSignoutMessage(centerId, logoutParameter);
        SaResult result = ssoClientTemplate.pushMessageAsSaResult(message);

        // 如果 sso-server 响应的状态码非200，代表业务失败，将回应的 msg 字段作为异常抛出
        if (result.getCode() == null || SaResult.CODE_SUCCESS != result.getCode()) {
            throw new SaSsoException(result.getMsg()).setCode(SaSsoErrorCode.CODE_30006);
        }

        // 极端场景下，sso-server 中心的单点注销可能并不会通知到当前 client 端，所以这里需要再补一刀
        if (stpLogic.isLogin()) {
            stpLogic.logout(loginId, logoutParameter);
        }
        return _ssoLogoutBack(req, res);
    }

    /**
     * 封装：校验ticket，取出loginId，如果 ticket 无效则抛出异常 （适用于模式二或模式三）
     *
     * @param ticket ticket码
     * @return SaCheckTicketResult
     */
    public SaCheckTicketResult checkTicket(String ticket) {
        return checkTicket(ticket, null);
    }

    /**
     * 封装：校验ticket，取出loginId，如果 ticket 无效则抛出异常 （适用于模式二或模式三）
     *
     * @param ticket ticket码
     * @param currUri 当前路由的uri，用于计算单点注销回调地址 （如果是使用模式二，可以填写null）
     * @return SaCheckTicketResult
     */
    public SaCheckTicketResult checkTicket(String ticket, String currUri) {
        SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();

        // 两种模式：
        //		isHttp=true：模式三，使用 http 请求从认证中心校验ticket
        //		isHttp=false：模式二，直连 redis 中校验 ticket
        if (cfg.getIsHttp()) {
            return _checkTicketByHttp(ticket, currUri);
        } else {
            return _checkTicketByRedis(ticket);
        }
    }

    /**
     * 校验 ticket，http 请求方式
     * @param ticket /
     * @param currUri /
     * @return /
     */
    public SaCheckTicketResult _checkTicketByHttp(String ticket, String currUri) {
        SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();
        ApiName apiName = ssoClientTemplate.getApiName();
        ParamName paramName = ssoClientTemplate.getParamName();

        // 计算当前 sso-client 的单点注销回调地址
        String ssoLogoutCall = null;
        if (cfg.getRegLogoutCall()) {
            // 如果配置了回调地址，就使用配置的值：
            if (SaFoxUtil.isNotEmpty(cfg.getCurrSsoLogoutCall())) {
                ssoLogoutCall = cfg.getCurrSsoLogoutCall();
            } else if (SaFoxUtil.isNotEmpty(currUri)) {
                // 如果提供了当前 uri，则根据此值来计算：
                ssoLogoutCall = SaHolder.getRequest().getUrl().replace(currUri, apiName.getSsoLogoutCall());
            } else {
                // 否则视为不注册单点注销回调地址
                log.info("视为不注册单点注销回调地址");
            }
        }

        // 发起请求
        SaSsoMessage message = ssoClientTemplate.buildCheckTicketMessage(ticket, ssoLogoutCall);
        SaResult result = ssoClientTemplate.pushMessageAsSaResult(message);

        // 如果 sso-server 响应的状态码非200，代表业务失败，将回应的 msg 字段作为异常抛出
        if (result.getCode() == null || result.getCode() != SaResult.CODE_SUCCESS) {
            throw new SaSsoException(result.getMsg()).setCode(SaSsoErrorCode.CODE_30005);
        }

        // 构建返回结果
        SaCheckTicketResult ctr = new SaCheckTicketResult();
        ctr.setLoginId(result.get(paramName.getLoginId()));
        ctr.setTokenValue(result.get(paramName.getTokenValue(), String.class));
        ctr.setDeviceId(result.get(paramName.getDeviceId(), String.class));
        ctr.setRemainTokenTimeout(result.get(paramName.getRemainTokenTimeout(), Long.class));
        ctr.setRemainSessionTimeout(result.get(paramName.getRemainSessionTimeout(), Long.class));
        ctr.setResult(result);

        // 转换 loginId 和 centerId
        ctr.setCenterId(ctr.getLoginId());
        ctr.setLoginId(ssoClientTemplate.getStrategy().getConvertCenterIdToLoginId().run(ctr.getCenterId()));

        return ctr;
    }

    /**
     * 校验 ticket，直连 redis 方式
     * @param ticket /
     * @return /
     */
    public SaCheckTicketResult _checkTicketByRedis(String ticket) {
        // 直连 redis 校验 ticket
        // 		注意此处调用了 SaSsoServerProcessor 处理器里的方法，
        // 		这意味着如果你的 sso-server 端重写了 SaSsoServerProcessor 里的部分方法，
        // 		而在当前 sso-client 没有按照相应格式重写 SaSsoClientProcessor 里的方法，
        // 		可能会导致调用失败（注意是可能，而非一定，主要取决于你是否改变了数据读写格式），
        // 		解决方案为：在当前 sso-client 端也按照 sso-server 端的格式重写 SaSsoClientProcessor 里的方法

        StpLogic stpLogic = ssoClientTemplate.getStpLogicOrGlobal();
        TicketModel ticketModel = new SaSsoServerTemplate().checkTicketParamAndDelete(ticket, ssoClientTemplate.getClient());

        SaCheckTicketResult ctr = new SaCheckTicketResult();
        ctr.setLoginId(ticketModel.getLoginId());
        ctr.setTokenValue(ticketModel.getTokenValue());
        ctr.setDeviceId(stpLogic.getLoginDeviceIdByToken(ticketModel.getTokenValue()));
        ctr.setRemainTokenTimeout(stpLogic.getTokenTimeout(ticketModel.getTokenValue()));
        ctr.setRemainSessionTimeout(stpLogic.getSessionTimeoutByLoginId(ticketModel.getLoginId()));
        ctr.setResult(null);

        // 转换 loginId 和 centerId
        ctr.setCenterId(ctr.getLoginId());
        ctr.setLoginId(ssoClientTemplate.getStrategy().getConvertCenterIdToLoginId().run(ctr.getCenterId()));

        return ctr;
    }

    /**
     * 封装：单点注销成功后返回结果
     * @param req SaRequest对象
     * @param res SaResponse对象
     * @return 返回结果
     */
    public Object _ssoLogoutBack(SaRequest req, SaResponse res) {
        return SaSsoProcessorHelper.ssoLogoutBack(req, res, ssoClientTemplate.getParamName());
    }

}
