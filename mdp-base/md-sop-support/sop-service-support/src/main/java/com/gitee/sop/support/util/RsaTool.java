package com.gitee.sop.support.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * RSA加解密工具。 公私钥生成工具
 *
 * @author henhen6
 */
public class RsaTool {
    private static final String RSA_ALGORITHM = "RSA";

    private final KeyFormat keyFormat;
    private final KeyLength keyLength;

    public RsaTool(KeyFormat keyFormat, KeyLength keyLength) {
        this.keyFormat = keyFormat;
        this.keyLength = keyLength;
    }

    /**
     * 将PKCS8编码的私钥转换为PKCS1编码
     * @param privateKeyData PKCS8编码的私钥
     * @return PKCS1编码的私钥
     * @throws Exception 异常
     */
    public static String convertPkcs8ToPkcs1(byte[] privateKeyData) throws Exception {
        PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privateKeyData);
        ASN1Encodable encodable = pkInfo.parsePrivateKey();
        ASN1Primitive primitive = encodable.toASN1Primitive();
        byte[] privateKeyPKCS1 = primitive.getEncoded();
        return Base64.encodeBase64String(privateKeyPKCS1);
    }

    /**
     * ASCII码转BCD码
     */
    public static byte[] asciiToBcd(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = ascToBcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : ascToBcd(ascii[j++]) & 0xff) + (bcd[i] << 4));
        }
        return bcd;
    }

    /**
     * ASC转换为BCD
     * @param asc asc
     * @return bcd
     */
    public static byte ascToBcd(byte asc) {
        byte bcd;
        if ((asc >= '0') && (asc <= '9')) {
            bcd = (byte) (asc - '0');
        } else if ((asc >= 'A') && (asc <= 'F')) {
            bcd = (byte) (asc - 'A' + 10);
        } else if ((asc >= 'a') && (asc <= 'f')) {
            bcd = (byte) (asc - 'a' + 10);
        } else {
            bcd = (byte) (asc - 48);
        }
        return bcd;
    }

    /**
     * 创建公钥私钥
     *
     * @return 返回公私钥对
     * @throws Exception 异常
     */
    public KeyStore createKeys() throws Exception {
        KeyPairGenerator keyPairGeno = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGeno.initialize(keyLength.getLength());
        KeyPair keyPair = keyPairGeno.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        KeyStore keyStore = new KeyStore();
        if (this.keyFormat == KeyFormat.PKCS1) {
            keyStore.setPublicKey(Base64.encodeBase64String(publicKey.getEncoded()));
            keyStore.setPrivateKey(convertPkcs8ToPkcs1(privateKey.getEncoded()));
        } else {
            keyStore.setPublicKey(Base64.encodeBase64String(publicKey.getEncoded()));
            keyStore.setPrivateKey(Base64.encodeBase64String(privateKey.getEncoded()));
        }
        return keyStore;
    }

    /**
     * 获取公钥对象
     *
     * @param pubKeyData 公钥
     * @return 返回公钥对象
     * @throws Exception 异常
     */
    public RSAPublicKey getPublicKey(byte[] pubKeyData) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取公钥对象
     *
     * @param pubKey 公钥
     * @return 返回私钥对象
     * @throws Exception 异常
     */
    public RSAPublicKey getPublicKey(String pubKey) throws Exception {
        return getPublicKey(Base64.decodeBase64(pubKey));

    }

    /**
     * 获取私钥对象
     *
     * @param priKey 私钥
     * @return 私钥对象
     * @throws Exception 异常
     */
    public RSAPrivateKey getPrivateKey(String priKey) throws Exception {
        return getPrivateKey(Base64.decodeBase64(priKey));
    }

    /**
     * 通过私钥byte[]将公钥还原，适用于RSA算法
     *
     * @param keyBytes key字节数据
     * @return 返回私钥
     * @throws Exception 异常
     */
    public RSAPrivateKey getPrivateKey(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

    }


    /**
     * 拆分数组
     */
    public byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    public enum KeyLength {
        /**
         * 秘钥长度：1024
         */
        LENGTH_1024(1024),
        /**
         * 秘钥长度：2048
         */
        LENGTH_2048(2048);
        private final int length;

        KeyLength(int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }
    }

    public enum KeyFormat {
        /**
         * java适用
         */
        PKCS8(1, "RSA", "PKCS8(Java适用)"),
        /**
         * 非java适用
         */
        PKCS1(2, "RSA/ECB/PKCS1Padding", "PKCS1(非Java适用)");

        private final Integer code;
        private final String cipherAlgorithm;
        private final String desc;

        KeyFormat(Integer code, String cipherAlgorithm, String desc) {
            this.code = code;
            this.cipherAlgorithm = cipherAlgorithm;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getCipherAlgorithm() {
            return cipherAlgorithm;
        }

        public String getDesc() {
            return desc;
        }

        public static KeyFormat of(Integer value) {
            for (KeyFormat keyFormat : KeyFormat.values()) {
                if (Objects.equals(value, keyFormat.code)) {
                    return keyFormat;
                }
            }
            return PKCS8;
        }
    }

    public static class KeyStore {
        private String publicKey;
        private String privateKey;

        public String getPublicKey() {
            return publicKey;
        }

        public KeyStore setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public KeyStore setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        @Override
        public String toString() {
            return "KeyStore{publicKey='" + publicKey + "', privateKey='" + privateKey + "'}";
        }
    }


}
