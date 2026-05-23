package top.mddata.base.json.util;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JSON 构建工具
 *
 * @see ObjectMapper
 *
 * @author henhen
 * @since 2026年05月22日
 */
public class JSONBuilder {

    private static final ObjectMapper OBJECT_MAPPER = SpringUtil.getBean(ObjectMapper.class);
    private final ObjectNode rootNode;

    private JSONBuilder() {
        this.rootNode = OBJECT_MAPPER.createObjectNode();
    }

    /**
     * 开始构建
     *
     * @return {@link JSONBuilder }
     */
    public static JSONBuilder builder() {
        return new JSONBuilder();
    }

    /**
     * 添加 字符串
     *
     * @param key   key 值
     * @param value 值
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, String value) {
        Objects.requireNonNull(key, "键不能为 null");
        if (value != null) {
            rootNode.put(key, value);
        }
        return this;
    }

    /**
     * 添加 int
     *
     * @param key   key 值
     * @param value 值
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, int value) {
        Objects.requireNonNull(key, "键不能为 null");
        rootNode.put(key, value);
        return this;
    }

    /**
     * 添加 long
     *
     * @param key   key 值
     * @param value 值
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, long value) {
        Objects.requireNonNull(key, "键不能为 null");
        rootNode.put(key, value);
        return this;
    }

    /**
     * 添加 布尔
     *
     * @param key   key 值
     * @param value 值
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, boolean value) {
        Objects.requireNonNull(key, "键不能为 null");
        rootNode.put(key, value);
        return this;
    }

    /**
     * 添加 浮点
     *
     * @param key   key 值
     * @param value 值
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, double value) {
        Objects.requireNonNull(key, "键不能为 null");
        rootNode.put(key, value);
        return this;
    }

    /**
     * 添加 json
     *
     * @param key   key 值
     * @param value 值
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, JsonNode value) {
        Objects.requireNonNull(key, "键不能为 null");
        if (value != null) {
            rootNode.set(key, value);
        }
        return this;
    }

    /**
     * 添加 Object
     *
     * @param key   key 值
     * @param value 值
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, Object value) {
        Objects.requireNonNull(key, "键不能为 null");
        if (value != null) {
            rootNode.set(key, OBJECT_MAPPER.valueToTree(value));
        }
        return this;
    }

    /**
     * 添加 List 到 JSON
     *
     * @param key  key 值
     * @param list list 参数
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, List<?> list) {
        Objects.requireNonNull(key, "键不能为 null");
        if (list != null) {
            ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
            for (Object item : list) {
                arrayNode.add(OBJECT_MAPPER.valueToTree(item));
            }
            rootNode.set(key, arrayNode);
        }
        return this;
    }

    /**
     * 添加 Map 到 JSON
     *
     * @param key key 值
     * @param map map 参数
     * @return {@link JSONBuilder }
     */
    public JSONBuilder add(String key, Map<?, ?> map) {
        Objects.requireNonNull(key, "键不能为 null");
        if (map != null) {
            ObjectNode objectNode = OBJECT_MAPPER.valueToTree(map);
            rootNode.set(key, objectNode);
        }
        return this;
    }

    /**
     * 构建
     *
     * @return {@link JsonNode }
     */
    public JsonNode build() {
        return rootNode;
    }

    /**
     * 构建 json 字符串
     *
     * @return {@link String }
     */
    public String buildString() {
        try {
            return rootNode.toString();
        } catch (Exception e) {
            throw new RuntimeException("构建 JSON 字符串失败", e);
        }
    }
}
