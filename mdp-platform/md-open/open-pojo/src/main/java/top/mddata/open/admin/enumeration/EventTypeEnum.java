package top.mddata.open.admin.enumeration;

import com.mybatisflex.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import top.mddata.base.interfaces.BaseEnum;

/**
 * 事件类型
 * [1-事件推送 2-接口回调]
 *
 * @author henhen6
 * @date 2025年11月20日19:53:37
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "事件类型-枚举")
public enum EventTypeEnum implements BaseEnum<Integer> {

    EVENT_PUSH(1, "事件推送"),
    CALLBACK(2, "接口回调");
    /**
     * 资源类型
     */
    @EnumValue
    private Integer code;

    /**
     * 资源描述
     */
    private String desc;


}
