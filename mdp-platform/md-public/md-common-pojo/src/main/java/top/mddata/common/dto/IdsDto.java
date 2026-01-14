package top.mddata.common.dto;

import com.alibaba.fastjson2.JSON;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

/**
 * 通用 id 表单对象
 * @author henhen6
 */
@Data
@Schema(description = "id集合")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdsDto {
    @NotEmpty(message = "请填写主键")
    @Schema(description = "主键")
    private Collection<? extends Serializable> ids;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
