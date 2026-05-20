package top.mddata.console.vo.system;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import top.mddata.console.entity.system.base.RequestLogBase;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 请求日志 VO类（通常用作Controller出参）。
 *
 * @author henhen6
 * @since 2026-05-08 12:35:58
 */
@Accessors(chain = true)
@Data
@FieldNameConstants
@Schema(description = "请求日志Vo")
@Table(RequestLogBase.TABLE_NAME)
public class RequestLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Schema(description = "主键")
    private Long id;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;
    /**
     * 国家
     */
    @Schema(description = "国家")
    private String ipCountry;
    /**
     * 区域
     */
    @Schema(description = "区域")
    private String ipRegion;
    /**
     * 省
     */
    @Schema(description = "省")
    private String ipProvince;
    /**
     * 市
     */
    @Schema(description = "市")
    private String ipCity;
    /**
     * 运营商
     */
    @Schema(description = "运营商")
    private String ipIsp;
    /**
     * 是否异常
     */
    @Schema(description = "是否异常")
    private Boolean abnormal;

    /**
     * 日志类型
     * [1-查询 2-新增 3-修改 4-删除 9-其他]
     */
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
    @Schema(description = "操作人")
    private String userName;

    /**
     * 类路径
     */
    @Schema(description = "类路径")
    private String classPath;

    /**
     * 方法名
     */
    @Schema(description = "方法名")
    private String methodName;

    /**
     * 请求地址
     */
    @Schema(description = "请求地址")
    private String httpUri;

    /**
     * 请求类型
     * #HttpMethod{GET:GET请求;POST:POST请求;PUT:PUT请求;DELETE:DELETE请求;PATCH:PATCH请求;TRACE:TRACE请求;HEAD:HEAD请求;OPTIONS:OPTIONS请求;}
     */
    @Schema(description = "请求类型")
    private String httpMethod;

    /**
     * 操作描述
     */
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
    @Schema(description = "浏览器请求头")
    private String ua;
    /**
     * 浏览器名称
     */
    @Schema(description = "浏览器名称")
    private String browserName;

    /**
     * 浏览器版本
     */
    @Schema(description = "浏览器版本")
    private String browserVersion;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;
    /**
     * 调用链
     */
    @Schema(description = "调用链")
    private String trace;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private Long createdBy;


    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    private String requestParam;

    /**
     * 返回值
     */
    @Schema(description = "返回值")
    private String responseBody;

    /**
     * 异常堆栈
     */
    @Schema(description = "异常堆栈")
    private String exceptionStack;

    /**
     * 请求线程变量
     */
    private String httpThreadLocal;

}
