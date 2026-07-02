package top.mddata.sdk.core.aes.pojo;

/**
 * MDP平台推送事件和回调的表单参数
 *
 * 使用于 兼容模式和安全模式
 *
 * @author henhen
 * @since 2026/7/2 15:40
 */
public class PushSecureFormParam extends PushFormParam {

    /**
     * 加密类型
     *
     * 支持加密方式：安全模式、兼容模式
     */
    private String encryptType;
    /**
     * 消息签名，MDP服务器会验证签名
     *
     * 支持加密方式：安全模式、兼容模式
     * */
    private String msgSignature;

    public String getEncryptType() {
        return encryptType;
    }

    public PushSecureFormParam setEncryptType(String encryptType) {
        this.encryptType = encryptType;
        return this;
    }

    public String getMsgSignature() {
        return msgSignature;
    }

    public PushSecureFormParam setMsgSignature(String msgSignature) {
        this.msgSignature = msgSignature;
        return this;
    }
}
