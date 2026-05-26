package top.mddata.base.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import top.mddata.base.converter.String2DateConverter;
import top.mddata.base.converter.String2LocalDateConverter;
import top.mddata.base.converter.String2LocalDateTimeConverter;
import top.mddata.base.converter.String2LocalTimeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;


/**
 * 基础配置类
 *
 * @author henhen6
 * @date 2019-06-22 22:53
 */
public abstract class BaseConfig {
    /**
     * 解决 @RequestParam(value = "date") Date date
     * date 类型参数 格式问题
     */
    @Bean
    public Converter<String, Date> dateConvert() {
        return new String2DateConverter();
    }

    /**
     * 解决 @RequestParam(value = "time") LocalDate time
     */
    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new String2LocalDateConverter();
    }

    /**
     * 解决 @RequestParam(value = "time") LocalTime time
     */
    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return new String2LocalTimeConverter();
    }

    /**
     * 解决 @RequestParam(value = "time") LocalDateTime time
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new String2LocalDateTimeConverter();
    }

    //---------------------------------------序列化配置end----------------------------------------------

}
