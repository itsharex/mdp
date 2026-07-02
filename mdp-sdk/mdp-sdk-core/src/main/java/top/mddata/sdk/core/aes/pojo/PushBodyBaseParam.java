package top.mddata.sdk.core.aes.pojo;

/**
 * MDP平台推送事件和回调的 请求体参数
 *
 * @author henhen
 * @since 2026/7/2 16:21
 */
public class PushBodyBaseParam {
    /** 应用的appKey */
    private String appKey;

    public String getAppKey() {
        return appKey;
    }

    public PushBodyBaseParam setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }
}
