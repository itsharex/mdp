package top.mddata.common.dto;

import com.alibaba.fastjson2.JSON;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 通用 id 表单对象
 * @author henhen6
 */
@Data
@Schema(description = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdDto {
    @NotNull(message = "请填写主键")
    @Schema(description = "主键")
    private Long id;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
