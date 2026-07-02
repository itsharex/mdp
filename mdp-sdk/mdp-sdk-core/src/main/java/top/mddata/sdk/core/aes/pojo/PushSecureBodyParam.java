package top.mddata.sdk.core.aes.pojo;

/**
 * MDP平台推送事件和回调的 安全模式 请求体参数
 *
 * @author henhen
 * @since 2026/7/2 16:21
 */
public class PushSecureBodyParam extends PushBodyBaseParam {
    /** 加密后的内容 */
    private String encrypt;

    public String getEncrypt() {
        return encrypt;
    }

    public PushSecureBodyParam setEncrypt(String encrypt) {
        this.encrypt = encrypt;
        return this;
    }
}
