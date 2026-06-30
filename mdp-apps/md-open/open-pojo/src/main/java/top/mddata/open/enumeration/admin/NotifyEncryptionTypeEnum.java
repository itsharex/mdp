package top.mddata.open.enumeration.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import top.mddata.base.interfaces.BaseEnum;

/**
 * 加密模式
 * [0-明文模式 1-兼容模式 2-安全模式]
 * 参考微信消息加解密方案：
 * - 明文模式：不加密，消息体明文收发
 * - 兼容模式：明文和密文共存
 * - 安全模式：纯密文，推荐使用
 *
 * @author henhen
 * @since 2026/1/4 12:24
 */
@Getter
@AllArgsConstructor
@Schema(description = "加密模式-枚举")
public enum NotifyEncryptionTypeEnum implements BaseEnum<Integer> {
    PLAINTEXT(0, "明文模式"),
    COMPATIBLE(1, "兼容模式"),
    SECURE(2, "安全模式");
    private final Integer code;
    private final String desc;

}
