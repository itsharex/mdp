package top.mddata.base.mybatisflex.listener;

import com.mybatisflex.annotation.SetListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 字段权限
 *
 * 1. 字段清空
 * 2. 字段脱敏
 *
 * @author henhen
 * @since 2026/3/13 00:39
 */
@Slf4j
public class FieldPermissionsOnSetListener implements SetListener {

    /**
     * 1. 先查询[当前菜单]下配置的所有字段权限
     * 2. 先查询[当前用户]在[当前菜单]下拥有的字段权限
     *
     *
     * @param entity   实体类
     * @param property 属性名
     * @param value    属性值
     * @return
     */

    @Override
    public Object onSet(Object entity, String property, Object value) {

        boolean isTarget = false;
        try {
            isTarget = entity != null && Class.forName("top.mddata.console.message.entity.MsgTask").isInstance(entity);
        } catch (ClassNotFoundException e) {
            log.warn("Cannot load the '{}'. Cause by ", "top.mddata.console.message.entity.MsgTask", e);
        }

        if (isTarget && property.equals("title")) {
            value = "[加密]" + value;
        }


//        Field updatedTimeField = ReflectUtil.getField(param.getClass(), SuperEntity.UPDATED_AT);
//        if (updatedTimeField != null) {
//            Object fieldValue = ReflectUtil.getFieldValue(param, updatedTimeField);
//            if (fieldValue == null) {
//                ReflectUtil.setFieldValue(param, updatedTimeField, LocalDateTime.now());
//            }
//        }

        return value;
    }
}
