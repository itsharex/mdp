package top.mddata.base.json.enums;

/**
 * 大数值序列化模式
 *
 * @author henhen
 * @since 2.12.1
 */
public enum BigNumberSerializeMode {

    /**
     * 超过 JS 范围的数值转为 {@link String} 类型，否则保持原类型
     * <p>
     * JS：Number.MIN_SAFE_INTEGER：-9007199254740991L <br />
     * JS：Number.MAX_SAFE_INTEGER：9007199254740991L
     * </p>
     */
    FLEXIBLE,

    /**
     * 统一转为 {@link String} 类型
     */
    TO_STRING,

    /**
     * 不操作（不建议）
     * <p>
     * 注意：超过 JS 范围的数值会损失精度，例如：8014753905961037835 会被转为 8014753905961038000
     * </p>
     */
    NO_OPERATE,
}
