package top.mddata.workbench.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 登录参数
 *
 * @author henhen6
 * @date 2020年01月05日22:18:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "RegisterByUsernameDto", description = "账号注册参数")
public class RegisterByUsernameDto extends RegisterVO {

    @Schema(description = "账号")
    @Size(max = 255, message = "账号长度不能超过{max}")
    @NotEmpty(message = "请填写账号")
    private String username;

    @Schema(description = "密码")
    @NotEmpty(message = "请填写密码")
    @Size(min = 6, max = 64, message = "密码长度不能小于{min}且超过{max}个字符")
    private String password;

    @Schema(description = "确认密码")
    @NotEmpty(message = "请填写确认密码")
    @Size(min = 6, max = 64, message = "确认密码长度不能小于{min}且超过{max}个字符")
    private String confirmPassword;

}
