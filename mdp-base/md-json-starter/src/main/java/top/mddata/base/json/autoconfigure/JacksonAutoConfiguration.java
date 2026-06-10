package top.mddata.base.json.autoconfigure;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import top.mddata.base.boot.handler.GeneralPropertySourceFactory;
import top.mddata.base.json.module.DateJacksonModule;
import top.mddata.base.json.module.NumberJacksonModule;

import java.util.TimeZone;

/**
 * Jackson 自动配置
 *
 * @author henhen
 * @since 1.0.0
 */
@AutoConfigureBefore(org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class)
@EnableConfigurationProperties(JacksonExtensionProperties.class)
@PropertySource(value = "classpath:default-json-jackson.yml", factory = GeneralPropertySourceFactory.class)
public class JacksonAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JacksonAutoConfiguration.class);
    private final JacksonExtensionProperties properties;

    public JacksonAutoConfiguration(JacksonExtensionProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            DateJacksonModule javaTimeModule = new DateJacksonModule();
            SimpleModule bigNumberModule = new NumberJacksonModule();

            builder.timeZone(TimeZone.getDefault());
            builder.modules(javaTimeModule, bigNumberModule);
            log.debug("自动配置“Jackson”已完成初始化。");
        };
    }

//    @Bean
//    @Primary
//    @ConditionalOnClass(ObjectMapper.class)
//    @ConditionalOnMissingBean
//    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
//        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
//        objectMapper
//                .setLocale(Locale.CHINA)
//                //去掉默认的时间戳格式
//                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//                // 时区
//                .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
//                //Date参数日期格式
//                .setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.CHINA))
//
//                //该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
//                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
//                // 忽略不能转义的字符
//                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true)
//                //在使用spring boot + jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
//                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//                //忽略未知字段
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                //单引号处理
//                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        // 注册自定义模块
//        objectMapper.registerModule(new DateJacksonModule()).registerModule(new NumberJacksonModule()).findAndRegisterModules();
//        return objectMapper;
//    }

}
