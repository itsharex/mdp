package top.mddata.base.base.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * <p>
 * 包含 id、createdAt、createdBy 字段的基础实体。
 * 所有需要这些公共字段的数据库表实体都应继承此类。
 * </p>
 *
 * <p>包含的字段：</p>
 * <ul>
 *   <li>id - 主键ID</li>
 *   <li>createdAt - 创建时间</li>
 *   <li>createdBy - 创建人ID</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * public class User extends BaseEntity&lt;Long&gt; {
 *     private String username;
 *     private String email;
 *     // ... 其他字段
 * }
 * </pre>
 *
 * @param <T> 主键类型
 * @author henhen6
 * @since 2019/05/05
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class BaseEntity<T> implements Serializable {
    // ==================== 字段常量定义 ====================
    
    /**
     * 主键ID字段名
     */
    public static final String ID_FIELD = "id";
    
    /**
     * 创建时间 - 实体字段名
     */
    public static final String CREATED_AT = "createdAt";
    
    /**
     * 创建时间 - 数据库字段名
     */
    public static final String CREATED_AT_FIELD = "created_at";
    
    /**
     * 创建人ID - 实体字段名
     */
    public static final String CREATED_BY = "createdBy";
    
    /**
     * 创建人ID - 数据库字段名
     */
    public static final String CREATED_BY_FIELD = "created_by";
    
    /**
     * 删除人 - 实体字段名
     */
    public static final String DELETED_BY = "deletedBy";
    
    /**
     * 删除人 - 数据库字段名
     */
    public static final String DELETED_BY_FIELD = "deleted_by";
    
    /**
     * 删除时间 - 实体字段名
     */
    public static final String DELETED_AT = "deletedAt";
    
    /**
     * 删除时间 - 数据库字段名
     */
    public static final String DELETED_AT_FIELD = "deleted_at";
    
    /**
     * 创建人所在公司 - 实体字段名
     */
    public static final String CREATED_BY_COMPANY = "createdByCompany";
    
    /**
     * 创建人所在公司 - 数据库字段名
     */
    public static final String CREATED_BY_COMPANY_FIELD = "created_by_company";
    
    /**
     * 创建人所在部门 - 实体字段名
     */
    public static final String CREATED_BY_DEPT = "createdByDept";
    
    /**
     * 创建人所在部门 - 数据库字段名
     */
    public static final String CREATED_BY_DEPT_FIELD = "created_by_dept";

    /**
     * 序列化版本号
     */
    @Serial
    private static final long serialVersionUID = -4603650115461757622L;

    /**
     * 主键ID
     * <p>
     * 使用 MyBatis-Flex 的 UID 生成器自动生成。
     * 在更新操作时，此字段不能为空。
     * </p>
     */
    @Schema(description = "主键")
    @NotNull(message = "id不能为空", groups = Update.class)
    @Id(keyType = KeyType.Generator, value = "uid")
    private T id;

    /**
     * 创建时间
     * <p>
     * 记录数据创建的时间，由系统自动填充。
     * </p>
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 创建人ID
     * <p>
     * 记录创建该数据的用户ID，由系统自动填充。
     * </p>
     */
    @Schema(description = "创建人ID")
    private T createdBy;

    /**
     * 保存验证组
     * <p>
     * 用于在新增数据时进行参数验证。
     * 继承自 Jakarta Validation 的 Default 验证组。
     * </p>
     */
    public interface Save extends Default {

    }

    /**
     * 更新验证组
     * <p>
     * 用于在更新数据时进行参数验证。
     * 继承自 Jakarta Validation 的 Default 验证组。
     * 在更新操作中，ID 字段不能为空。
     * </p>
     */
    public interface Update extends Default {

    }
}
