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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.model.TicketModel;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.util.SaFoxUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * SSO 模板方法类 （Server端）
 *
 * @author click33
 * @since 1.38.0
 */
@Slf4j
public class SaSsoServerTemplate extends SaSsoTemplate {
    /**
     * 查询 ticket ，如果 ticket 无效则返回 null
     *
     * @param ticket Ticket码
     * @return 账号id
     */
    public TicketModel getTicket(String ticket) {
        if (SaFoxUtil.isEmpty(ticket)) {
            return null;
        }
        return SaManager.getSaTokenDao().getObject(splicingTicketModelSaveKey(ticket), TicketModel.class);
    }

    /**
     * 校验 Ticket，无效 ticket 会抛出异常
     *
     * @param ticket Ticket码
     * @return /
     */
    public TicketModel checkTicket(String ticket) {
        TicketModel ticketModel = getTicket(ticket);
        if (ticketModel == null) {
            throw new SaSsoException("无效 ticket : " + ticket).setCode(SaSsoErrorCode.CODE_30004);
        }
        return ticketModel;
    }


    /**
     * 校验 Ticket 码，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
     * @param ticket Ticket码
     * @return 账号id
     */
    public TicketModel checkTicketParamAndDelete(String ticket) {
        return checkTicketParamAndDelete(ticket, SaSsoConsts.CLIENT_WILDCARD);
    }

    /**
     * 校验 Ticket，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
     *
     * @param ticket Ticket码
     * @param client client 标识
     * @return /
     */
    public TicketModel checkTicketParamAndDelete(String ticket, String client) {
        TicketModel ticketModel = checkTicket(ticket);

        // 校验 client 参数是否正确，即：创建 ticket 的 client 和当前校验 ticket 的 client 是否一致
        String ticketClient = ticketModel.getClient();
        if (SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
            // 如果提供的是通配符，直接越过 client 校验
        } else if (SaFoxUtil.isEmpty(client) && SaFoxUtil.isEmpty(ticketClient)) {
            // 如果提供的和期望的两者均为空，则通过校验
        } else {
            // 开始详细比对
            if (SaFoxUtil.notEquals(client, ticketClient)) {
                throw new SaSsoException("该 ticket 不属于 client=" + client + ", ticket 值: " + ticket).setCode(SaSsoErrorCode.CODE_30011);
            }
        }

        // 删除 ticket 信息，使其只有一次性有效
        deleteTicket(ticket);
        deleteTicketIndex(client, ticketModel.getLoginId());

        //
        return ticketModel;
    }

    // ticket 索引

    /**
     * 删除 Ticket
     * @param ticket Ticket码
     */
    public void deleteTicket(String ticket) {
        if (ticket == null) {
            return;
        }
        SaManager.getSaTokenDao().deleteObject(splicingTicketModelSaveKey(ticket));
    }

    /**
     * 删除 Ticket 索引
     *
     * @param client 应用标识
     * @param loginId 账号id
     */
    public void deleteTicketIndex(String client, Object loginId) {
        if (loginId == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketIndexKey(client, loginId));
    }

    // ------------------- 返回相应key -------------------

    /**
     * 拼接key：TicketModel
     * @param ticket ticket值
     * @return key
     */
    public String splicingTicketModelSaveKey(String ticket) {
        return getStpLogicOrGlobal().getConfigOrGlobal().getTokenName() + ":ticket:" + ticket;
    }

    /**
     * 拼接key：Ticket 索引
     *
     * @param client 应用标识
     * @param id 账号id
     * @return key
     */
    public String splicingTicketIndexKey(String client, Object id) {
        if (SaFoxUtil.isEmpty(client) || SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
            client = SaSsoConsts.CLIENT_ANON;
        }
        return getStpLogicOrGlobal().getConfigOrGlobal().getTokenName() + ":ticket-index:" + client + ":" + id;
    }

}
