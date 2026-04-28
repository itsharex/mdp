package top.mddata.api.oepn.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gitee.sop.support.dto.ApiConfig;
import com.gitee.sop.support.exception.SignException;
import com.gitee.sop.support.message.ApiResponse;
import com.gitee.sop.support.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回调演示类
 * @author henhen
 * @since 2025/12/18 10:04
 */
@RestController
@Slf4j
public class NotifyCallbackController {
    // 平台下发的公钥：  sop_isv_keys 表的 public_key_platform 字段
    String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0dAa+aJQ/QFs2eq/eHxb6U6b26sBs6+8FNQOF16S8QLMzqi44bJHKPv0nTF7UkOQpCftbDYlf0vWIrB61PSi2/woGGDKRUCQSMJcWCUPO8L4GzN9qMf9I08k7rGrkt4gnb95gdQT1/Sg7KBQX5kOdOqXtcNktLJ4LxdZ3blYXSH/0cM7VB3urAgkuE80a+UdCz7DEZPpjiH9vdWpYevtp1bUfIGEdt9jS39roIsthMyABn6ty+aoruf5VbWz/K1hesWUQlwBUC8BRGrdShBPncNk++Mpf7xcIE6tgc67nY4T/2SExfjpJmZBKFe0DrtZRFr5umYMEh1Ocldd7d+/lwIDAQAB";

    /**
     * 默认配置的回调地址
     * @param content
     * @return
     */
    @PostMapping("/anyUser/notify/callback")
    public ApiResponse callback(@RequestBody String content) {
        log.info("收到回调通知, content={}", content);
        JSONObject jsonObject = JSON.parseObject(content);
        // 签名校验
        if (!checkSign(jsonObject)) {
            return ApiResponse.error("签名校验错误");
        }
        log.info("签名验证通过，处理业务逻辑");
        String method = jsonObject.getString("method");
        // 判断业务类型，处理不同业务
        switch (method) {
            // 处理订单创建回调
            case "shop.order.create":
                createOrder(jsonObject);
                break;
            case "shop.order.close":
                closeOrder(jsonObject);
                break;
            default:
                closeOrder(jsonObject);
        }
        // 返回code = 0 表示成功
        return ApiResponse.success("");
    }

    private static void closeOrder(JSONObject jsonObject) {
        // 处理订单关闭回调
        String bizContent = jsonObject.getString("biz_content");
        log.info("业务参数，bizContent={}", bizContent);
    }

    private static void createOrder(JSONObject jsonObject) {
        JSONObject bizContent = jsonObject.getJSONObject("biz_content");
        log.info("业务参数，bizContent={}", bizContent);
    }

    /**
     * 自定义指定的回调地址
     *
     * @param content
     * @return
     */
    @PostMapping("notify/callback22")
    public ApiResponse callback22(@RequestBody String content) {
        log.info("callback22");
        return callback(content);
    }


    private boolean checkSign(JSONObject jsonObject) {
        try {
            ApiConfig apiConfig = new ApiConfig();
            return SignUtil.rsaCheckV2(jsonObject, publicKey, "UTF-8", SignUtil.RSA2, apiConfig);
        } catch (SignException e) {
            log.error("签名校验错误, jsonObject={}", jsonObject, e);
            return false;
        }
    }

}
