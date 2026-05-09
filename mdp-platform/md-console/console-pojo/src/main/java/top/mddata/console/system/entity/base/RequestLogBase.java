package top.mddata.console.system.entity.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import top.mddata.base.base.entity.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 请求日志实体类。
 *
 * @author henhen6
 * @since 2026-05-08 12:35:58
 */
@FieldNameConstants
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RequestLogBase extends BaseEntity<Long> implements Serializable {
    /** 表名称 */
    public static final String TABLE_NAME = "mdc_request_log";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * 国家
     */
    private String ipCountry;
    /**
     * 区域
     */
    private String ipRegion;
    /**
     * 省
     */
    private String ipProvince;
    /**
     * 市
     */
    private String ipCity;
    /**
     * 运营商
     */
    private String ipIsp;

    /**
     * 是否异常
     */
    private Boolean abnormal;

    /**
     * 日志类型
     * [1-查询 2-新增 3-修改 4-删除 9-其他]
     */
    private String logType;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 操作人
     */
    private String userName;

    /**
     * 类路径
     */
    private String classPath;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 请求地址
     */
    private String httpUri;

    /**
     * 请求类型
     * #HttpMethod{GET:GET请求;POST:POST请求;PUT:PUT请求;DELETE:DELETE请求;PATCH:PATCH请求;TRACE:TRACE请求;HEAD:HEAD请求;OPTIONS:OPTIONS请求;}
     */
    private String httpMethod;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 消耗时间
     */
    private Long consumingTime;

    /**
     * 浏览器请求头
     */
    private String ua;

    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 调用链
     */
    private String trace;
    /**
     * 请求线程变量
     */
    private String httpThreadLocal;


}
