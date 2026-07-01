package com.gitee.sop.support.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author 六如
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DefaultOpenContext extends OpenContext implements Serializable {
    private static final long serialVersionUID = -3218354527911979685L;

    /**
     * appKey
     */
    private String appKey;
    /**
     * 调用日志ID
     *
     */
    private Long callLogId;
    /** 应用ID */
    private Long appId;

    /**
     * apiName
     */
    private String apiName;

    /**
     * version
     */
    private String version;

    /**
     * accessToken,没有返回null
     */
    private String accessToken;

    /**
     * 客户端ip
     */
    private String clientIp;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 唯一请求id
     */
    private String traceId;

    /**
     * locale
     */
    private Locale locale;
    /**
     * charset
     */
    private String charset;

    public void initContext() {
        this.setContext(this);
    }

    public void remove() {
        this.clear();
    }
}
