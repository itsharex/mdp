package top.mddata.console.service.message.strategy.impl.sms;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.mddata.base.model.Kv;
import top.mddata.base.util.StrPool;
import top.mddata.console.entity.message.InterfaceConfig;
import top.mddata.console.entity.message.MsgTask;
import top.mddata.console.entity.message.MsgTaskRecipient;
import top.mddata.console.entity.message.MsgTemplate;
import top.mddata.console.service.message.strategy.AbstractMsgTaskStrategy;
import top.mddata.console.service.message.strategy.dto.MsgResult;
import top.mddata.console.service.message.strategy.dto.MsgTaskParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 腾讯短信
 * @author henhen
 * @since 2025/12/29 10:36
 */
@Slf4j
@Service("tencentSmsMsgTaskStrategyImpl")
@RequiredArgsConstructor
public class TencentSmsMsgTaskStrategyImpl extends AbstractMsgTaskStrategy {
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MsgResult exec(MsgTaskParam msgParam) {
        validParam(msgParam);
        Map<String, String> propertyParam = msgParam.getPropertyParam();
        // 多个手机号用逗号分割
        List<MsgTaskRecipient> taskRecipientList = msgParam.getRecipientList();
        List<String> phoneNumberList = taskRecipientList.stream().map(MsgTaskRecipient::getRecipient).toList();
        String phoneNumbers = taskRecipientList.stream().map(MsgTaskRecipient::getRecipient).collect(Collectors.joining(StrPool.COMMA));

        if (propertyParam.get("debug") != null && Convert.toBool(propertyParam.get("debug"))) {
            log.info("已忽略短信发送: {}", JSON.toJSONString(phoneNumbers));
            return MsgResult.builder().result(true).remarks("已忽略发送").build();
        }

        MsgTemplate msgTemplate = msgParam.getMsgTemplate();
        MsgTask msgTask = msgParam.getMsgTask();

        // 解析参数
        List<Kv> paramList = JSON.parseArray(msgTask.getParam(), Kv.class);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        for (Kv kv : paramList) {
            params.put(kv.getKey(), kv.getValue());
        }
        String template = msgTemplate.getSmsTemplateId();

        InterfaceConfig interfaceConfig = msgParam.getInterfaceConfig();
        SmsBlend smsBlend = SmsFactory.getSmsBlend(interfaceConfig.getKey());
        SmsResponse smsResponse = smsBlend.massTexting(phoneNumberList, template, params);

        MsgResult msgResult = new MsgResult();
        msgResult.setResult(smsResponse);
        msgResult.setTitle(msgTask.getTitle());
        msgResult.setContent(template);
        return msgResult;
    }

    @Override
    public boolean isSuccess(MsgResult result) {
        if (result.getResult() instanceof Boolean res) {
            return res;
        }
        SmsResponse smsResponse = (SmsResponse) result.getResult();
        return smsResponse.isSuccess();
    }
}
