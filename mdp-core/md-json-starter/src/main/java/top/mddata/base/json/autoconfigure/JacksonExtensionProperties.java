package top.mddata.base.json.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import top.mddata.base.json.enums.BigNumberSerializeMode;

/**
 * Jackson 扩展配置属性
 *
* @author henhen
 */
@ConfigurationProperties("spring.jackson")
public class JacksonExtensionProperties {

    /**
     * 大数值序列化模式
     */
    private BigNumberSerializeMode bigNumberSerializeMode = BigNumberSerializeMode.FLEXIBLE;

    public BigNumberSerializeMode getBigNumberSerializeMode() {
        return bigNumberSerializeMode;
    }

    public void setBigNumberSerializeMode(BigNumberSerializeMode bigNumberSerializeMode) {
        this.bigNumberSerializeMode = bigNumberSerializeMode;
    }
}
