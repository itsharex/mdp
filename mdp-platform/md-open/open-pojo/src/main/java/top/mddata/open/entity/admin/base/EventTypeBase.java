package top.mddata.open.entity.admin.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import top.mddata.base.base.entity.SuperEntity;

import java.io.Serial;
import java.io.Serializable;

/**
 * 事件类型实体类。
 *
 * @author henhen6
 * @since 2026-01-12 21:28:36
 */
@FieldNameConstants
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EventTypeBase extends SuperEntity<Long> implements Serializable {
    /** 表名称 */
    public static final String TABLE_NAME = "mdo_event_type";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件编码
     */
    private String code;

    /**
     * 事件名称
     */
    private String name;

    /**
     * 事件描述
     */
    private String remarks;

    /**
     * 状态
     */
    private Boolean state;
    /**
     * 排序
     */
    private Integer weight;

}
