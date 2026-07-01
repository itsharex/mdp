package com.gitee.sop.support.aes;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * JSON 消息提取与生成。
 * 替代微信 XMLParse，使用 JSON 格式传输加密消息。
 */
class JsonParse {

    /**
     * 从 JSON 消息体中提取密文和应用标识。
     *
     * @param jsonText JSON 格式的消息文本
     * @return 长度为 2 的数组：[0]=encrypt, [1]=appKey
     * @throws AesException JSON 解析失败或字段缺失
     */
    public static String[] extract(String jsonText) throws AesException {
        try {
            JSONObject jsonObject = JSON.parseObject(jsonText);
            String encrypt = jsonObject.getString("encrypt");
            String appKey = jsonObject.getString("appKey");
            if (encrypt == null || appKey == null) {
                throw new AesException(AesException.PARSE_JSON_ERROR);
            }
            return new String[]{encrypt, appKey};
        } catch (AesException e) {
            throw e;
        } catch (Exception e) {
            throw new AesException(AesException.PARSE_JSON_ERROR);
        }
    }

    /**
     * 生成加密消息的 JSON 格式。
     *
     * @param encrypt   密文
     * @param appKey    应用标识
     * @param signature 安全签名
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return JSON 格式的消息
     */
    public static String generate(String encrypt, String appKey, String signature,
                                  String timestamp, String nonce) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("encrypt", encrypt);
        jsonObject.put("appKey", appKey);
        jsonObject.put("msgSignature", signature);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("nonce", nonce);
        return jsonObject.toJSONString();
    }
}
