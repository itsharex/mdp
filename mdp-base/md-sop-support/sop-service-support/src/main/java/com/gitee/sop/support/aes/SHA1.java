package com.gitee.sop.support.aes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * SHA-1 签名计算。
 * 将 token、timestamp、nonce、encrypt 四个参数字典序排序后拼接，再做 SHA-1 哈希。
 */
class SHA1 {

    /**
     * 计算安全签名。
     *
     * @param token     签名令牌
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param encrypt   密文（明文模式下传空字符串）
     * @return 40 字符的十六进制 SHA-1 签名
     * @throws AesException 签名计算失败
     */
    public static String getSHA1(String token, String timestamp, String nonce, String encrypt)
            throws AesException {
        try {
            String[] array = new String[]{token, timestamp, nonce, encrypt};
            StringBuilder sb = new StringBuilder();
            Arrays.sort(array);
            for (int i = 0; i < 4; i++) {
                sb.append(array[i]);
            }
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(sb.toString().getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();

            StringBuilder hexstr = new StringBuilder();
            for (byte b : digest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            throw new AesException(AesException.COMPUTE_SIGNATURE_ERROR);
        }
    }
}
