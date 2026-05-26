package top.mddata.base.log.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.mddata.base.annotation.log.RequestLog;
import top.mddata.base.util.StrPool;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 日志工具类
 *
 * @author henhen6
 * @date 2019-04-28 11:30
 */
@Slf4j
public final class LogUtil {
    private LogUtil() {
    }

    /***
     * 获取操作信息
     */
    public static String getDescribe(JoinPoint point) {
        RequestLog annotation = getTargetAnnotation(point);
        if (annotation == null) {
            return StrPool.EMPTY;
        }
        return annotation.value();
    }

    public static String getDescribe(RequestLog annotation) {
        if (annotation == null) {
            return StrPool.EMPTY;
        }
        return annotation.value();
    }

    /**
     * 优先从子类获取 @RequestLog：
     * 优先级：子类方法 > 子类类 > 父类方法 > 父类类 > 父接口方法/接口
     *
     * 1，若子类重写了该方法，有标记就记录日志，没标记就忽略日志
     * 2，若子类没有重写该方法，就从父类获取，父类有标记就记录日志，没标记就忽略日志
     */
    public static RequestLog getTargetAnnotation(JoinPoint point) {
        try {
            if (point.getSignature() instanceof MethodSignature ms) {
                Method method = ms.getMethod();
                if (method != null) {
                    // 1. 优先获取【方法上的注解】（支持继承+合并，子类覆盖父类）
                    RequestLog methodLog = AnnotatedElementUtils.getMergedAnnotation(method, RequestLog.class);
                    if (Objects.nonNull(methodLog)) {
                        return methodLog;
                    }

                    // 2. 方法无注解，获取【类上的注解】（支持父类/接口，子类覆盖）
                    Class<?> targetClass = point.getTarget().getClass();
                    return AnnotatedElementUtils.getMergedAnnotation(targetClass, RequestLog.class);
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("获取 {}.{} 的 @RequestLog 注解失败", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), e);
            return null;
        }
    }

}
