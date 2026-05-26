package com.gitee.sop.support.message;

import com.gitee.sop.support.exception.OpenException;

import java.util.Locale;

/**
 * 定义国际化消息
 *
 * @author 六如
 */
public interface I18nMessage {

    /**
     * i18n配置文件key
     */
    String getConfigKey();

    default IError getError(Locale locale, Object... params) {
        throw new OpenException(this, "请实现此方法");
    }

}
