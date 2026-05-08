package top.mddata.console.system.entity;

import com.mybatisflex.annotation.Table;
import top.mddata.console.system.entity.base.RequestLogDetailBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 请求日志实体类。
 * DO类：数据对象，可以在关联查询时，再次添加字段，重新生成代码时，忽略此文件。
 *
 * @author henhen6
 * @since 2026-05-08 12:35:58
 */
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(RequestLogDetailBase.TABLE_NAME)
public class RequestLogDetail extends RequestLogDetailBase {
}
