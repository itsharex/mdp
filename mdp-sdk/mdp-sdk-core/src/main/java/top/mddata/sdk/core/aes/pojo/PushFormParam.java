package top.mddata.sdk.core.aes.pojo;

/**
 * MDP平台推送事件和回调的表单参数
 *
 * 适用于 明文模式
 *
 * @author henhen
 * @since 2026/7/2 15:40
 */
public class PushFormParam {
    /**
     * 加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     *
     * 支持加密方式：明文模式、安全模式、兼容模式
     * */
    private String signature;
    /**
     * 时间戳
     *
     * 支持加密方式：明文模式、安全模式、兼容模式
     * */
    private String timestamp;
    /**
     * 随机数
     *
     * 支持加密方式：明文模式、安全模式、兼容模式
     * */
    private String nonce;

    public String getSignature() {
        return signature;
    }

    public PushFormParam setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public PushFormParam setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public PushFormParam setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }
}
