package com.gitee.sop.support.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * 消息推送加解密工具类。
 * 参考微信消息加解密方案，支持明文模式、兼容模式、安全模式。
 *
 * <p>AES加密细节：
 * <ul>
 *   <li>AESKey = Base64Decode(encodingAesKey + "=")，32字节</li>
 *   <li>IV = AESKey的前16字节</li>
 *   <li>明文 = random(16B) + msgLen(4B网络字节序) + msg + appId</li>
 *   <li>密文 = Base64(AES_CBC_PKCS7(明文, AESKey, IV))</li>
 * </ul>
 *
 * @author henhen6
 * @since 2026-06-27
 */
public final class NotifyPushUtil {

    private NotifyPushUtil() {
    }

    /**
     * AES加密消息体
     *
     * @param plaintext        明文JSON字符串
     * @param encodingAesKey   43字符的AES密钥
     * @param appKey            应用标识，解密时会校验
     * @return Base64编码的密文
     */
    public static String encrypt(String plaintext, String encodingAesKey, String appKey) {
        try {
            byte[] aesKey = decodeAesKey(encodingAesKey);
            byte[] iv = Arrays.copyOfRange(aesKey, 0, 16);

            // 构造明文：random(16B) + msgLen(4B) + msg + appId
            byte[] randomBytes = RandomUtil.randomString(16).getBytes(StandardCharsets.UTF_8);
            byte[] msgBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] appIdBytes = appKey.getBytes(StandardCharsets.UTF_8);
            byte[] msgLenBytes = ByteBuffer.allocate(4).putInt(msgBytes.length).array();

            byte[] fullPlain = new byte[randomBytes.length + msgLenBytes.length + msgBytes.length + appIdBytes.length];
            int offset = 0;
            System.arraycopy(randomBytes, 0, fullPlain, offset, randomBytes.length);
            offset += randomBytes.length;
            System.arraycopy(msgLenBytes, 0, fullPlain, offset, msgLenBytes.length);
            offset += msgLenBytes.length;
            System.arraycopy(msgBytes, 0, fullPlain, offset, msgBytes.length);
            offset += msgBytes.length;
            System.arraycopy(appIdBytes, 0, fullPlain, offset, appIdBytes.length);

            // AES-CBC加密，PKCS7填充
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] padded = pkcs7Pad(fullPlain, 32);
            byte[] encrypted = cipher.doFinal(padded);

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("消息加密失败", e);
        }
    }

    /**
     * AES解密消息体
     *
     * @param encrypt        Base64编码的密文
     * @param encodingAesKey 43字符的AES密钥
     * @param appId          应用标识，解密后会校验是否匹配
     * @return 解密后的明文JSON字符串
     */
    public static String decrypt(String encrypt, String encodingAesKey, String appId) {
        try {
            byte[] aesKey = decodeAesKey(encodingAesKey);
            byte[] iv = Arrays.copyOfRange(aesKey, 0, 16);

            byte[] encrypted = Base64.getDecoder().decode(encrypt);

            // AES-CBC解密
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            // 去除PKCS7填充
            byte[] unpadded = pkcs7Unpad(decrypted);

            // 解析：random(16B) + msgLen(4B) + msg + appId
            // 跳过16字节随机串
            byte[] msgLenBytes = Arrays.copyOfRange(unpadded, 16, 20);
            int msgLen = ByteBuffer.wrap(msgLenBytes).getInt();

            byte[] msgBytes = Arrays.copyOfRange(unpadded, 20, 20 + msgLen);
            String msg = new String(msgBytes, StandardCharsets.UTF_8);

            // 校验appId
            byte[] appIdBytes = Arrays.copyOfRange(unpadded, 20 + msgLen, unpadded.length);
            String decryptedAppId = new String(appIdBytes, StandardCharsets.UTF_8);
            if (!appId.equals(decryptedAppId)) {
                throw new RuntimeException("appId不匹配，期望：" + appId + "，实际：" + decryptedAppId);
            }

            return msg;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("消息解密失败", e);
        }
    }

    /**
     * 计算消息签名
     * 将 token、timestamp、nonce、encrypt 四个参数字典序排序后拼接，再做SHA1
     *
     * @param token     签名令牌
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param encrypt   密文（明文模式下传空字符串）
     * @return SHA1签名（40字符十六进制字符串）
     */
    public static String calcSignature(String token, String timestamp, String nonce, String encrypt) {
        try {
            String[] arr = {token, timestamp, nonce, encrypt};
            Arrays.sort(arr);
            StringBuilder sb = new StringBuilder();
            for (String s : arr) {
                if (s != null) {
                    sb.append(s);
                }
            }
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("签名计算失败", e);
        }
    }

    /**
     * 验证消息签名
     *
     * @param token        签名令牌
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param encrypt      密文（明文模式下传空字符串）
     * @param msgSignature 待验证的签名
     * @return 签名是否匹配
     */
    public static boolean verifySignature(String token, String timestamp, String nonce,
                                          String encrypt, String msgSignature) {
        String calculated = calcSignature(token, timestamp, nonce, encrypt);
        return calculated.equals(msgSignature);
    }

    /**
     * 生成随机数
     */
    public static String generateNonce() {
        return RandomUtil.randomString(16);
    }

    /**
     * 获取当前时间戳（秒）
     */
    public static String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 生成消息唯一ID
     */
    public static String generateMessageId() {
        return IdUtil.getSnowflakeNextIdStr();
    }

    /**
     * 构建安全模式的请求体（仅密文 + appKey）。
     * 签名相关字段（msg_signature/timestamp/nonce/encrypt_type）通过 URL 参数传递。
     *
     * @param encrypt 密文
     * @param appKey  应用标识
     * @return JSON格式的请求体，字段：encrypt / appKey
     */
    public static JSONObject buildEncryptedBody(String encrypt, String appKey) {
        JSONObject body = new JSONObject();
        body.put("encrypt", encrypt);
        body.put("appKey", appKey);
        return body;
    }

    /**
     * 构建兼容模式的请求体（明文字段展开 + 密文 + appKey）。
     * 签名相关字段（msg_signature/timestamp/nonce/encrypt_type）通过 URL 参数传递。
     *
     * @param plaintext 明文JSON字符串（业务字段会展开到顶层）
     * @param encrypt   密文
     * @param appKey    应用标识
     * @return JSON格式的请求体
     */
    public static JSONObject buildCompatibleBody(String plaintext, String encrypt, String appKey) {
        JSONObject body = new JSONObject();
        // 兼容模式：明文业务字段展开到顶层
        JSONObject plainData = JSON.parseObject(plaintext);
        body.putAll(plainData);
        body.put("encrypt", encrypt);
        body.put("appKey", appKey);
        return body;
    }

    /**
     * 生成 EncodingAESKey（43字符）。
     * 算法：生成 32 字节随机数据 → Base64 编码（44字符） → 去掉末尾 '=' 填充（43字符）。
     * 解码时 {@code Base64Decode(encodingAesKey + "=")} 即可还原 32 字节 AESKey。
     */
    public static String generateEncodingAesKey() {
        byte[] bytes = new byte[32];
        new java.security.SecureRandom().nextBytes(bytes);
        // Base64 标准编码 32 字节 = 44 字符（含 1 个 '=' 填充），去掉 '=' 得 43 字符
        return Base64.getEncoder().encodeToString(bytes).replace("=", "");
    }

    /**
     * 生成 Token
     * 由平台生成后提供给开发者
     */
    public static String generateToken() {
        return RandomUtil.randomString(32);
    }

    /**
     * 将 EncodingAESKey 解码为 32字节 AESKey
     *
     * @throws IllegalArgumentException encodingAesKey 不是合法 Base64 时抛出
     */
    private static byte[] decodeAesKey(String encodingAesKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encodingAesKey + "=");
            if (decoded.length != 32) {
                throw new IllegalArgumentException(
                        "encodingAesKey 解码后应为 32 字节，实际为 " + decoded.length + " 字节");
            }
            return decoded;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "encodingAesKey 不是合法的 Base64 字符串（长度需为 43，仅含 A-Za-z0-9+/）: "
                            + encodingAesKey, e);
        }
    }

    /**
     * PKCS7 填充
     *
     * @param data    待填充数据
     * @param blockSize 块大小（32字节）
     * @return 填充后的数据
     */
    private static byte[] pkcs7Pad(byte[] data, int blockSize) {
        int padding = blockSize - (data.length % blockSize);
        byte[] pad = new byte[padding];
        Arrays.fill(pad, (byte) padding);
        byte[] result = new byte[data.length + padding];
        System.arraycopy(data, 0, result, 0, data.length);
        System.arraycopy(pad, 0, result, data.length, padding);
        return result;
    }

    /**
     * PKCS7 去填充
     *
     * @param data 带填充的数据
     * @return 去除填充后的数据
     */
    private static byte[] pkcs7Unpad(byte[] data) {
        int padding = data[data.length - 1] & 0xFF;
        if (padding < 1 || padding > 32) {
            return data;
        }
        return Arrays.copyOfRange(data, 0, data.length - padding);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
