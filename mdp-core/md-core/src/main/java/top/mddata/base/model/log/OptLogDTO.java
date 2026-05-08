package top.mddata.base.model.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import top.mddata.base.annotation.log.RequestLog;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * 系统日志
 * </p>
 *
 * @author henhen6
 * @since 2019-07-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@Accessors(chain = true)
public class OptLogDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 操作IP
     */
    private String ipAddress;

    private Long userId;

    /**
     * 日志链路追踪id日志标志
     */
    private String trace;

    /**
     * 是否异常
     */
    private Boolean abnormal;
    /**
     * 日志类型
     * [1-查询 2-新增 3-修改 4-删除 9-其他]
     */
    private RequestLog.LogType logType;

    /**
     * 操作人
     */
    private String userName;

    /**
     * 操作描述
     */
    private String description;

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
     * 请求参数
     */
    private String requestParam;

    /**
     * 返回值
     */
    private String responseBody;

    /**
     * 异常描述
     */
    private String exceptionStack;

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
     * 浏览器
     */
    private String ua;

    private Long createdBy;
    private Long createdOrgId;
    private String token;


}
