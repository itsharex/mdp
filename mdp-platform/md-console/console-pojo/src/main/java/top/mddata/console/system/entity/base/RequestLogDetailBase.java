package top.mddata.console.system.entity.base;

import java.io.Serializable;
import top.mddata.base.base.entity.SuperEntity;

import java.io.Serial;

import lombok.experimental.FieldNameConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;

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
public class RequestLogDetailBase extends SuperEntity<Long> implements Serializable {
    /** 表名称 */
    public static final String TABLE_NAME = "mdc_request_log_detail";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 返回值
     */
    private String responseBody;

    /**
     * 异常堆栈
     */
    private String exceptionStack;

}
