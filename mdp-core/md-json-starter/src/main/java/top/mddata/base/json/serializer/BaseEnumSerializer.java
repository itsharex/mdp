package top.mddata.base.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import top.mddata.base.interfaces.BaseEnum;

import java.io.IOException;

/**
 * 枚举接口 BaseEnum 序列化器
 *
 * @see BaseEnum
 *
  * @author henhen
 * @since 2.4.0
 */
@JacksonStdImpl
public class BaseEnumSerializer extends JsonSerializer<BaseEnum> {

    /**
     * 静态实例
     */
    public static final BaseEnumSerializer SERIALIZER_INSTANCE = new BaseEnumSerializer();

    @Override
    public void serialize(BaseEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeObject(value.getCode());
    }
}
