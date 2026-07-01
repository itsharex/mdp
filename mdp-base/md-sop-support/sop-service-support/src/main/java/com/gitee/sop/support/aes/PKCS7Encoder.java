package com.gitee.sop.support.aes;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * PKCS7 填充/去填充（块大小 32 字节）。
 * AES-CBC 模式要求明文长度为块大小的整数倍，不足时按 PKCS7 规则填充。
 */
class PKCS7Encoder {
    static final Charset CHARSET = StandardCharsets.UTF_8;
    static final int BLOCK_SIZE = 32;

    /**
     * 对明文进行 PKCS7 填充。
     *
     * @param count 待填充数据的字节数
     * @return 填充用的字节数组
     */
    static byte[] encode(int count) {
        int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
        if (amountToPad == 0) {
            amountToPad = BLOCK_SIZE;
        }
        char padChr = chr(amountToPad);
        return String.valueOf(padChr).repeat(amountToPad).getBytes(CHARSET);
    }

    /**
     * 去除解密后明文的 PKCS7 填充。
     *
     * @param decrypted 解密后的字节数组（含填充）
     * @return 去除填充后的字节数组
     */
    static byte[] decode(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    /**
     * 将数字转化为 ASCII 字符（用于 PKCS7 填充）。
     */
    static char chr(int a) {
        byte target = (byte) (a & 0xFF);
        return (char) target;
    }
}
