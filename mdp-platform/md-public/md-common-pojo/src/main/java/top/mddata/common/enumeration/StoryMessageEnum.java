package top.mddata.common.enumeration;

import com.gitee.sop.support.message.I18nMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author henhen
 */
@AllArgsConstructor
@Getter
public enum StoryMessageEnum implements I18nMessage {
    /**
     * 业务异常
     */
    PARAM_VALIDATION("biz.param-validation");

    private final String configKey;

}
