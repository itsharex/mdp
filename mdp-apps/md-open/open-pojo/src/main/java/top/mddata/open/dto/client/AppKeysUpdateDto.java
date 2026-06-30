package top.mddata.open.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 开发者修改应用通知配置
 *
 * @author henhen6
 * @since 2025/11/20 20:48
 */
@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "开发者修改应用通知配置")
public class AppKeysUpdateDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属应用
     */
    @NotNull(message = "请填写所属应用")
    @Schema(description = "所属应用")
    private Long appId;

    /**
     * 通知地址
     */
    @Size(max = 255, message = "通知地址长度不能超过{max}")
    @Schema(description = "通知地址")
    private String notifyUrl;

    /**
     * 加密模式
     * [0-明文模式 1-兼容模式 2-安全模式]
     */
    @Schema(description = "加密模式")
    private Integer notifyEncryptionType;
}
