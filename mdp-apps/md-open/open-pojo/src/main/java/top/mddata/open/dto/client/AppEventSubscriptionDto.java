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
import java.util.List;

/**
 * 应用秘钥修改
 * @author henhen6
 * @since 2025/11/20 20:48
 */
@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "应用修改自己的事件订阅")
public class AppEventSubscriptionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * appId
     */
    @NotNull(message = "请填写应用ID")
    @Schema(description = "应用ID")
    private Long appId;
    /**
     * 通知状态
     */
    @NotNull(message = "请填写通知状态")
    @Schema(description = "通知状态")
    private Boolean notifyState;


    /**
     * 通知地址
     */
    @Size(max = 255, message = "通知地址长度不能超过255")
    @Schema(description = "通知地址")
    private String notifyUrl;


    /**
     * 加密模式
     * [0-明文模式 1-兼容模式 2-安全模式]
     */
    @NotNull(message = "请填写加密模式")
    @Schema(description = "加密模式")
    private Integer notifyEncryptionType;


    /**
     * 签名校验令牌
     */
    @Schema(description = "签名校验令牌")
    private String notifyToken;

    /**
     * AES加解密密钥（43字符）
     */
    @Schema(description = "AES加解密密钥")
    private String notifyEncodingAesKey;

    @Schema(description = "订阅的事件")
    private List<Long> eventTypeIdList;
}
