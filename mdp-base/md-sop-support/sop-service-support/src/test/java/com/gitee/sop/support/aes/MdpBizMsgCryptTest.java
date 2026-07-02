package com.gitee.sop.support.aes;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MdpBizMsgCrypt 测试。
 * 覆盖：加解密往返、签名计算与校验、三种加密模式、边界条件、JSON 消息格式。
 *
 * @author henhen
 * @since 2026-07-01
 */
@DisplayName("MdpBizMsgCrypt 消息推送加解密测试")
class MdpBizMsgCryptTest {

    /** 43 字符的 EncodingAESKey */
    private static final String ENCODING_AES_KEY = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFG";

    /** 签名令牌 */
    private static final String TOKEN = "test_token_123";

    /** 应用标识 */
    private static final String APP_KEY = "12345";

    private MdpBizMsgCrypt crypt;

    @BeforeEach
    void setUp() throws AesException {
        crypt = new MdpBizMsgCrypt(TOKEN, ENCODING_AES_KEY, APP_KEY);
    }

    // ========== 加密/解密 ==========

    @Nested
    @DisplayName("加密与解密")
    class EncryptDecrypt {

        @Test
        @DisplayName("明文模式：encryptMsg 返回明文 JSON，signature 非空，msgSignature 为 null")
        void plaintextMode_encryptMsg() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_PLAINTEXT);

            assertNotNull(body.getString("type"));
            assertEquals("EVENT_PUSH", body.getString("type"));
            assertEquals("org.edit", body.getString("method"));
            assertNotNull(crypt.getSignature());
            assertNull(crypt.getMsgSignature());
        }

        @Test
        @DisplayName("兼容模式：encryptMsg 返回明文展开 + encrypt + appKey")
        void compatibleMode_encryptMsg() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_COMPATIBLE);

            assertEquals("EVENT_PUSH", body.getString("type"));
            assertEquals("org.edit", body.getString("method"));
            assertNotNull(body.getString("encrypt"));
            assertEquals(APP_KEY, body.getString("appKey"));
            assertNotNull(crypt.getMsgSignature());
        }

        @Test
        @DisplayName("安全模式：encryptMsg 返回仅 encrypt + appKey")
        void encryptedMode_encryptMsg() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);

            assertNull(body.getString("type"));
            assertNotNull(body.getString("encrypt"));
            assertEquals(APP_KEY, body.getString("appKey"));
            assertEquals(2, body.size());
            assertNotNull(crypt.getMsgSignature());
        }

        @Test
        @DisplayName("加密模式完整往返：encryptMsg + decryptMsg 应还原明文")
        void encryptAndDecryptMsg_roundTrip() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);
            String decrypted = crypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, crypt.getMsgSignature());

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("兼容模式完整往返：encryptMsg + decryptMsg 应还原明文")
        void compatibleMode_roundTrip() throws AesException {
            String plaintext = "{\"type\":\"CALLBACK\",\"method\":\"shop.order.create\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_COMPATIBLE);
            String decrypted = crypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, crypt.getMsgSignature());

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("中文字符加解密往返")
        void encryptAndDecrypt_chineseContent() throws AesException {
            String plaintext = "{\"name\":\"测试组织\",\"remark\":\"中文内容测试\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);
            String decrypted = crypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, crypt.getMsgSignature());

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("长文本加解密往返（超过 AES 块大小 32 字节）")
        void encryptAndDecrypt_longContent() throws AesException {
            StringBuilder sb = new StringBuilder("{\"data\":\"");
            for (int i = 0; i < 200; i++) {
                sb.append("abcdefghij");
            }
            sb.append("\"}");
            String plaintext = sb.toString();

            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);
            String decrypted = crypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, crypt.getMsgSignature());

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("空字符串加解密往返")
        void encryptAndDecrypt_emptyContent() throws AesException {
            String plaintext = "";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);
            String decrypted = crypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, crypt.getMsgSignature());

            assertEquals(plaintext, decrypted);
        }

        @Test
        @DisplayName("每次加密结果不同（随机 16 字节前缀不同）")
        void encrypt_producesDifferentCiphertextEachTime() throws AesException {
            String plaintext = "{\"test\":\"data\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body1 = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);
            JSONObject body2 = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);

            assertNotEquals(body1.getString("encrypt"), body2.getString("encrypt"));
        }

        @Test
        @DisplayName("解密时 appKey 不匹配应抛异常")
        void decryptMsg_appKeyMismatch_throwsException() throws AesException {
            String plaintext = "{\"test\":\"data\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);

            MdpBizMsgCrypt wrongCrypt = new MdpBizMsgCrypt(TOKEN, ENCODING_AES_KEY, "wrong_app_key");
            assertThrows(AesException.class,
                    () -> wrongCrypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, crypt.getMsgSignature()));
        }

        @Test
        @DisplayName("解密错误密文应抛异常")
        void decryptMsg_invalidCiphertext_throwsException() throws AesException {
            JSONObject body = new JSONObject();
            body.put("encrypt", "invalid_base64_data!!");
            body.put("appKey", APP_KEY);
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();
            String msgSignature = MdpBizMsgCrypt.calcSignature(TOKEN, timestamp, nonce, "invalid_base64_data!!");

            assertThrows(AesException.class,
                    () -> crypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, msgSignature));
        }

        @Test
        @DisplayName("验签失败应抛 VALIDATE_SIGNATURE_ERROR")
        void decryptMsg_wrongSignature_throwsException() throws AesException {
            String plaintext = "{\"test\":\"data\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);

            assertThrows(AesException.class,
                    () -> crypt.decryptMsg(body.getString("encrypt"), timestamp, nonce, "wrong_signature"));
        }

        @Test
        @DisplayName("encodingAesKey 非法应抛 ILLEGAL_AES_KEY")
        void constructor_illegalAesKey_throwsException() {
            AesException ex = assertThrows(AesException.class,
                    () -> new MdpBizMsgCrypt(TOKEN, "abcde", APP_KEY));
            assertEquals(AesException.ILLEGAL_AES_KEY, ex.getCode());
        }
    }

    // ========== 签名计算与校验 ==========

    @Nested
    @DisplayName("签名计算与校验")
    class Signature {

        @Test
        @DisplayName("同样参数计算出的签名应一致（幂等性）")
        void calcSignature_sameInputs_sameResult() {
            String timestamp = "1609459200";
            String nonce = "abc123";
            String encrypt = "encrypted_data";

            String sig1 = MdpBizMsgCrypt.calcSignature(TOKEN, timestamp, nonce, encrypt);
            String sig2 = MdpBizMsgCrypt.calcSignature(TOKEN, timestamp, nonce, encrypt);

            assertEquals(sig1, sig2);
            assertEquals(40, sig1.length(), "SHA1签名应为40字符十六进制");
        }

        @Test
        @DisplayName("签名与参数顺序无关（内部会排序）")
        void calcSignature_orderIndependent() {
            String timestamp = "1609459200";
            String nonce = "abc123";
            String encrypt = "encrypted_data";

            String sig1 = MdpBizMsgCrypt.calcSignature(TOKEN, timestamp, nonce, encrypt);
            String sig2 = MdpBizMsgCrypt.calcSignature(encrypt, nonce, timestamp, TOKEN);

            assertEquals(sig1, sig2);
        }

        @Test
        @DisplayName("任一参数变化，签名应不同")
        void calcSignature_differentInput_differentResult() {
            String base = MdpBizMsgCrypt.calcSignature(TOKEN, "1609459200", "abc123", "encrypt");

            assertNotEquals(base,
                    MdpBizMsgCrypt.calcSignature("other_token", "1609459200", "abc123", "encrypt"));
            assertNotEquals(base,
                    MdpBizMsgCrypt.calcSignature(TOKEN, "9999999999", "abc123", "encrypt"));
            assertNotEquals(base,
                    MdpBizMsgCrypt.calcSignature(TOKEN, "1609459200", "xyz789", "encrypt"));
            assertNotEquals(base,
                    MdpBizMsgCrypt.calcSignature(TOKEN, "1609459200", "abc123", "other_encrypt"));
        }

        @Test
        @DisplayName("verifySignature：正确签名返回 true")
        void verifySignature_correctSignature_returnsTrue() {
            String timestamp = "1609459200";
            String nonce = "abc123";
            String encrypt = "encrypted_data";
            String signature = MdpBizMsgCrypt.calcSignature(TOKEN, timestamp, nonce, encrypt);

            assertTrue(MdpBizMsgCrypt.verifySignature(TOKEN, timestamp, nonce, encrypt, signature));
        }

        @Test
        @DisplayName("verifySignature：错误签名返回 false")
        void verifySignature_wrongSignature_returnsFalse() {
            assertFalse(MdpBizMsgCrypt.verifySignature(
                    TOKEN, "1609459200", "abc123", "encrypted_data", "wrong_signature_value"));
        }

        @Test
        @DisplayName("明文模式：encrypt 传空字符串，签名仍可正常校验")
        void verifySignature_plaintextMode_encryptIsEmpty() {
            String timestamp = "1609459200";
            String nonce = "abc123";
            String signature = MdpBizMsgCrypt.calcSignature(TOKEN, timestamp, nonce, "");

            assertTrue(MdpBizMsgCrypt.verifySignature(TOKEN, timestamp, nonce, "", signature));
        }
    }

    // ========== 辅助方法 ==========

    @Nested
    @DisplayName("辅助方法")
    class HelperMethods {

        @Test
        @DisplayName("generateNonce 生成 16 字符随机串")
        void generateNonce_length16() {
            String nonce = MdpBizMsgCrypt.generateNonce();
            assertNotNull(nonce);
            assertEquals(16, nonce.length());
        }

        @Test
        @DisplayName("generateNonce 连续两次生成结果不同")
        void generateNonce_differentEachCall() {
            assertNotEquals(MdpBizMsgCrypt.generateNonce(), MdpBizMsgCrypt.generateNonce());
        }

        @Test
        @DisplayName("generateTimestamp 返回当前秒级时间戳")
        void generateTimestamp_currentSeconds() {
            long before = System.currentTimeMillis() / 1000;
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            long after = System.currentTimeMillis() / 1000;

            long ts = Long.parseLong(timestamp);
            assertTrue(ts >= before && ts <= after);
        }

        @Test
        @DisplayName("generateMessageId 返回非空字符串")
        void generateMessageId_notEmpty() {
            String id = MdpBizMsgCrypt.generateMessageId();
            assertNotNull(id);
            assertFalse(id.isEmpty());
        }

        @Test
        @DisplayName("generateEncodingAesKey 生成 43 字符")
        void generateEncodingAesKey_length43() {
            assertEquals(43, MdpBizMsgCrypt.generateEncodingAesKey().length());
        }

        @Test
        @DisplayName("generateToken 生成 32 字符")
        void generateToken_length32() {
            assertEquals(32, MdpBizMsgCrypt.generateToken().length());
        }
    }

    // ========== 请求体构建 ==========

    @Nested
    @DisplayName("请求体构建（JSON 格式）")
    class BuildBody {

        @Test
        @DisplayName("安全模式请求体：仅 encrypt + appKey（小写驼峰）")
        void encryptedMode_bodyContainsOnlyEncryptAndAppKey() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);

            assertNotNull(body.getString("encrypt"));
            assertEquals(APP_KEY, body.getString("appKey"));
            assertFalse(body.containsKey("type"));
            assertEquals(2, body.size());
        }

        @Test
        @DisplayName("兼容模式请求体：明文展开 + encrypt + appKey")
        void compatibleMode_bodyContainsPlainAndEncrypted() throws AesException {
            String plaintext = "{\"type\":\"CALLBACK\",\"method\":\"shop.order.create\",\"bizContent\":\"test\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_COMPATIBLE);

            assertEquals("CALLBACK", body.getString("type"));
            assertEquals("shop.order.create", body.getString("method"));
            assertEquals("test", body.getString("bizContent"));
            assertNotNull(body.getString("encrypt"));
            assertEquals(APP_KEY, body.getString("appKey"));
        }

        @Test
        @DisplayName("明文模式请求体：直接为明文 JSON 展开")
        void plaintextMode_bodyIsPlainJson() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\",\"appKey\":\"test\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_PLAINTEXT);

            assertEquals("EVENT_PUSH", body.getString("type"));
            assertEquals("org.edit", body.getString("method"));
            assertEquals("test", body.getString("appKey"));
            assertFalse(body.containsKey("encrypt"));
        }
    }

    // ========== URL 参数拼接 ==========

    @Nested
    @DisplayName("URL 参数拼接")
    class UrlParams {

        @Test
        @DisplayName("所有参数均存在时正确拼接")
        void appendUrlParams_allPresent() {
            String url = MdpBizMsgCrypt.appendUrlParams(
                    "https://example.com/callback", "sig123", "msgSig456", "1609459200", "abc123", "aes");

            assertTrue(url.contains("signature=sig123"));
            assertTrue(url.contains("msgSignature=msgSig456"));
            assertTrue(url.contains("timestamp=1609459200"));
            assertTrue(url.contains("nonce=abc123"));
            assertTrue(url.contains("encryptType=aes"));
            assertTrue(url.startsWith("https://example.com/callback?"));
        }

        @Test
        @DisplayName("msgSignature 为 null 时不拼接（明文模式）")
        void appendUrlParams_nullMsgSignature() {
            String url = MdpBizMsgCrypt.appendUrlParams(
                    "https://example.com/callback", "sig123", null, "1609459200", "abc123", null);

            assertTrue(url.contains("signature=sig123"));
            assertFalse(url.contains("msgSignature"));
            assertFalse(url.contains("encryptType"));
        }

        @Test
        @DisplayName("baseUrl 已含 ? 时用 & 连接")
        void appendUrlParams_existingQueryParam() {
            String url = MdpBizMsgCrypt.appendUrlParams(
                    "https://example.com/callback?foo=bar", "sig", null, "ts", "nc", null);

            assertTrue(url.contains("?foo=bar&signature=sig"));
        }
    }

    // ========== 三种加密模式完整流程 ==========

    @Nested
    @DisplayName("三种加密模式完整流程（JSON 格式）")
    class FullFlow {

        @Test
        @DisplayName("明文模式：body 直接传递明文，signature = SHA1(sort(token, ts, nonce, ''))")
        void plaintextMode_fullFlow() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\",\"appKey\":\"test\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_PLAINTEXT);
            String signature = crypt.getSignature();

            // 接收方验证签名
            assertTrue(MdpBizMsgCrypt.verifySignature(TOKEN, timestamp, nonce, "", signature));

            // 明文模式直接读取业务字段
            assertEquals("EVENT_PUSH", body.getString("type"));
            assertEquals("org.edit", body.getString("method"));
        }

        @Test
        @DisplayName("安全模式：加密+签名完整流程（签名通过 URL 参数传递）")
        void secureMode_fullFlow() throws AesException {
            String plaintext = "{\"type\":\"EVENT_PUSH\",\"method\":\"org.edit\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            // 发送方：encryptMsg → 构建 body（仅 encrypt + appKey）
            JSONObject sendBody = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);
            String msgSignature = crypt.getMsgSignature();
            String url = MdpBizMsgCrypt.appendUrlParams("https://example.com", null, msgSignature, timestamp, nonce, "aes");

            // 接收方：通过 URL 参数中的签名验证 → 从 body 取密文解密
            assertTrue(MdpBizMsgCrypt.verifySignature(
                    TOKEN, timestamp, nonce,
                    sendBody.getString("encrypt"),
                    msgSignature));

            String decrypted = crypt.decryptMsg(sendBody.getString("encrypt"), timestamp, nonce, msgSignature);
            assertEquals(plaintext, decrypted);

            // body 仅含 encrypt 和 appKey
            assertEquals(APP_KEY, sendBody.getString("appKey"));
            assertEquals(2, sendBody.size());
            // URL 含 msgSignature 和 encryptType
            assertTrue(url.contains("msgSignature="));
            assertTrue(url.contains("encryptType=aes"));
        }

        @Test
        @DisplayName("兼容模式：明文+密文共存完整流程")
        void compatibleMode_fullFlow() throws AesException {
            String plaintext = "{\"type\":\"CALLBACK\",\"method\":\"shop.order.create\",\"appKey\":\"test\"}";
            String timestamp = MdpBizMsgCrypt.generateTimestamp();
            String nonce = MdpBizMsgCrypt.generateNonce();

            // 发送方：encryptMsg → 构建 body（明文展开 + encrypt + appKey）
            JSONObject sendBody = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_COMPATIBLE);
            String msgSignature = crypt.getMsgSignature();

            // 接收方：通过 URL 参数中的签名验证 → 从 body 取密文解密
            assertTrue(MdpBizMsgCrypt.verifySignature(
                    TOKEN, timestamp, nonce,
                    sendBody.getString("encrypt"),
                    msgSignature));

            String decrypted = crypt.decryptMsg(sendBody.getString("encrypt"), timestamp, nonce, msgSignature);
            assertEquals(plaintext, decrypted);

            // 兼容模式也可直接读取明文业务字段
            assertEquals("CALLBACK", sendBody.getString("type"));
            assertEquals("shop.order.create", sendBody.getString("method"));
            assertEquals(APP_KEY, sendBody.getString("appKey"));
        }
    }

    // ========== verifyUrl ==========

    @Test
    @DisplayName("verifyUrl：正确签名不抛异常")
    void verifyUrl_correctSignature_noException() throws AesException {
        // 先用 crypt 加密一个字符串
        String plaintext = "echo_str_content";
        String timestamp = MdpBizMsgCrypt.generateTimestamp();
        String nonce = MdpBizMsgCrypt.generateNonce();

        JSONObject body = crypt.encryptMsg(plaintext, timestamp, nonce, MdpBizMsgCrypt.MODE_ENCRYPTED);
        String encrypted = body.getString("encrypt");
        String msgSignature = crypt.getMsgSignature();

        // verifyUrl 应该不抛异常
        String result = crypt.verifyUrl(msgSignature, timestamp, nonce, encrypted);
        assertEquals(plaintext, result);
    }

    @Test
    @DisplayName("verifyUrl：错误签名应抛 VALIDATE_SIGNATURE_ERROR")
    void verifyUrl_wrongSignature_throwsException() {
        String timestamp = MdpBizMsgCrypt.generateTimestamp();
        String nonce = MdpBizMsgCrypt.generateNonce();

        AesException ex = assertThrows(AesException.class,
                () -> crypt.verifyUrl("wrong_sig", timestamp, nonce, "some_encrypted_data"));
        assertEquals(AesException.VALIDATE_SIGNATURE_ERROR, ex.getCode());
    }
}
