package top.mddata.console.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import top.mddata.base.base.entity.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 请求日志 DTO（写入方法入参）。
 *
 * @author henhen6
 * @since 2026-05-08 12:35:58
 */
@Accessors(chain = true)
@Data
@FieldNameConstants
@Schema(description = "请求日志Dto")
public class RequestLogDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @NotNull(message = "请填写主键", groups = BaseEntity.Update.class)
    @Schema(description = "主键")
    private Long id;

    /**
     * IP地址
     */
    @Size(max = 50, message = "IP地址长度不能超过{max}")
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 是否异常
     */
    @Schema(description = "是否异常")
    private Boolean abnormal;

    /**
     * 日志类型
     * [1-查询 2-新增 3-修改 4-删除 9-其他]
     */
    @NotEmpty(message = "请填写日志类型")
    @Size(max = 1, message = "日志类型长度不能超过{max}")
    @Schema(description = "日志类型")
    private String logType;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;

    /**
     * 操作人
     */
    @Size(max = 50, message = "操作人长度不能超过{max}")
    @Schema(description = "操作人")
    private String userName;

    /**
     * 类路径
     */
    @Size(max = 255, message = "类路径长度不能超过{max}")
    @Schema(description = "类路径")
    private String classPath;

    /**
     * 方法名
     */
    @Size(max = 50, message = "方法名长度不能超过{max}")
    @Schema(description = "方法名")
    private String methodName;

    /**
     * 请求地址
     */
    @Size(max = 500, message = "请求地址长度不能超过{max}")
    @Schema(description = "请求地址")
    private String httpUri;

    /**
     * 请求类型
     * #HttpMethod{GET:GET请求;POST:POST请求;PUT:PUT请求;DELETE:DELETE请求;PATCH:PATCH请求;TRACE:TRACE请求;HEAD:HEAD请求;OPTIONS:OPTIONS请求;}
     */
    @Size(max = 10, message = "请求类型长度不能超过{max}")
    @Schema(description = "请求类型")
    private String httpMethod;

    /**
     * 操作描述
     */
    @Size(max = 255, message = "操作描述长度不能超过{max}")
    @Schema(description = "操作描述")
    private String description;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    /**
     * 消耗时间
     */
    @Schema(description = "消耗时间")
    private Long consumingTime;

    /**
     * 浏览器请求头
     */
    @Size(max = 500, message = "浏览器长度不能超过{max}")
    @Schema(description = "浏览器请求头")
    private String ua;

    /**
     * 调用链
     */
    @Size(max = 255, message = "调用链长度不能超过{max}")
    @Schema(description = "调用链")
    private String trace;


    /**
     * 请求参数
     */
    @Size(max = 536870911, message = "请求参数长度不能超过{max}")
    @Schema(description = "请求参数")
    private String requestParam;

    /**
     * 返回值
     */
    @Size(max = 536870911, message = "返回值长度不能超过{max}")
    @Schema(description = "返回值")
    private String responseBody;

    /**
     * 异常堆栈
     */
    @Size(max = 536870911, message = "异常堆栈长度不能超过{max}")
    @Schema(description = "异常堆栈")
    private String exceptionStack;

    /**
     * 请求线程变量
     */
    @Size(max = 1024, message = "异常堆栈长度不能超过{max}")
    @Schema(description = "请求线程变量")
    private String httpThreadLocal;
    private Long createdBy;
}
