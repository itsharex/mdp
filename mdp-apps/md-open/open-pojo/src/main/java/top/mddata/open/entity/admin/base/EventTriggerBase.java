package top.mddata.open.entity.admin.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import top.mddata.base.base.entity.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事件触发实体类。
 *
 * @author henhen6
 * @since 2026-01-12 21:29:13
 */
@FieldNameConstants
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EventTriggerBase extends BaseEntity<Long> implements Serializable {
    /** 表名称 */
    public static final String TABLE_NAME = "mdo_event_trigger";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 事件ID
     */
    private Long eventId;

    /**
     * 触发时间
     */
    private LocalDateTime triggerAt;

    /**
     * 事件内容
     */
    private String eventContent;

}
