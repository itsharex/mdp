package top.mddata.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取访问令牌入参
 *
 * @author henhen
 * @since 2026/7/1
 */
@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "获取访问令牌入参")
public class AccessTokenDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用标识
     */
    @NotBlank(message = "appKey不能为空")
    @Schema(description = "应用标识")
    private String appKey;

    /**
     * 应用秘钥
     */
    @NotBlank(message = "appSecret不能为空")
    @Schema(description = "应用秘钥")
    private String appSecret;

    /**
     * 是否强制刷新（设置为true时，废弃旧token并颁发新token）
     */
    @Schema(description = "是否强制刷新")
    private Boolean forceRefresh;
}
