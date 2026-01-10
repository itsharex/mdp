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
package cn.dev33.satoken.sso.name;

import lombok.Data;

/**
 * SSO 模块所有参数名称定义 
 *
 * @author click33
 * @since 1.32.0
 */
@Data
public class ParamName {

    /** redirect 参数名称 */
    private String redirect = "redirect";

    /** ticket 参数名称 */
    private String ticket = "ticket";

    /** back 参数名称 */
    private String back = "back";

    /** mode 参数名称 */
    private String mode = "mode";

    /** 账号 id */
    private String loginId = "loginId";

    /** client 应用标识 */
    private String client = "client";

    /** token 名称 */
    private String tokenName = "tokenName";

    /** token 值 */
    private String tokenValue = "tokenValue";

    /** 设备 id */
    private String deviceId = "deviceId";

    /** 接口参数签名秘钥 */
    private String secretkey = "secretkey";

    /** Client 端单点注销时 - 回调 URL 参数名称 */
    private String ssoLogoutCall = "ssoLogoutCall";

    /** 是否为超过 maxRegClient 触发的自动注销 */
    private String autoLogout = "autoLogout";

    private String name = "name";
    private String pwd = "pwd";

    private String timestamp = "timestamp";
    private String nonce = "nonce";
    private String sign = "sign";

    /** Session 剩余有效期 参数名称 */
    private String remainSessionTimeout = "remainSessionTimeout";

    /** token 剩余有效期 参数名称 */
    private String remainTokenTimeout = "remainTokenTimeout";

    /** 是否单设备 id 注销 */
    private String singleDeviceIdLogout = "singleDeviceIdLogout";


}
