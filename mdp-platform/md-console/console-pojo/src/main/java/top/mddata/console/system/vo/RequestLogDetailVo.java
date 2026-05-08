package top.mddata.console.system.vo;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import top.mddata.console.system.entity.base.RequestLogDetailBase;

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
@Table(RequestLogDetailBase.TABLE_NAME)
public class RequestLogDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @Id
    @Schema(description = "主键")
    private Long id;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private Long createdBy;

}
