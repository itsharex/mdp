package top.mddata.base.log.aspect;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.mddata.base.annotation.log.RequestLog;
import top.mddata.base.base.R;
import top.mddata.base.constant.ContextConstants;
import top.mddata.base.log.event.SysLogEvent;
import top.mddata.base.log.properties.OptLogProperties;
import top.mddata.base.log.util.LogUtil;
import top.mddata.base.log.util.ThreadLocalParam;
import top.mddata.base.model.log.OptLogDTO;
import top.mddata.base.utils.ContextUtil;
import top.mddata.base.utils.StrPool;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 操作日志使用spring event异步入库
 *
 * @author henhen6
 * @date 2019-07-01 15:15
 */
@Slf4j
@Aspect
public class SysLogAspect {
    public static final int MAX_LENGTH = 65535;
    private static final ThreadLocal<OptLogDTO> THREAD_LOCAL = new ThreadLocal<>();
    private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";
    private final OptLogProperties properties;
    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * 方法参数名解析器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public SysLogAspect(OptLogProperties properties) {
        this.properties = properties;
    }

    /***
     * 定义controller切入点拦截规则：拦截标记RequestLog注解和指定包下的方法
     * 2个表达式加起来才能拦截所有Controller 或者继承了BaseController的方法
     *
     * execution(public * top.mddata.base.base.controller.*.*(..)) 解释：
     * 第一个* 任意返回类型
     * 第二个* top.mddata.base.base.controller包下的所有类
     * 第三个* 类下的所有方法
     * ()中间的.. 任意参数
     *
     * \@annotation(top.mddata.base.annotation.log.RequestLog) 解释：
     * 标记了@RequestLog 注解的方法
     */
    @Pointcut("execution(public * top.mddata.base.mvcflex.controller.*.*(..)) || @annotation(top.mddata.base.annotation.log.RequestLog)")
    public void sysLogAspect() {

    }

    /**
     * 执行方法之前
     *
     * @param joinPoint 端点
     */
    @Before(value = "sysLogAspect()")
    public void doBefore(JoinPoint joinPoint) {
        tryCatch(val -> {
            RequestLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (check(sysLog)) {
                return;
            }
            OptLogDTO optLogDTO = buildOptLogDTO(joinPoint, sysLog);
            THREAD_LOCAL.set(optLogDTO);
        });
    }


    /**
     * 返回通知
     *
     * @param ret       返回值
     * @param joinPoint 端点
     */
    @AfterReturning(returning = "ret", pointcut = "sysLogAspect()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch(p -> {
            RequestLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (check(sysLog)) {
                return;
            }

            R r = Convert.convert(R.class, ret);
            OptLogDTO sysLogDTO = get();
            if (r == null) {
                sysLogDTO.setAbnormal(false);
                if (sysLog.response()) {
                    sysLogDTO.setResponseBody(getText(String.valueOf(ret == null ? StrPool.EMPTY : ret)));
                }
            } else {
                if (r.getIsSuccess()) {
                    sysLogDTO.setAbnormal(false);
                } else {
                    sysLogDTO.setAbnormal(true);
                    sysLogDTO.setExceptionStack(r.getMsg());
                }
                if (sysLog.response()) {
                    sysLogDTO.setResponseBody(getText(JSON.toJSONString(r)));
                }
            }

            publishEvent(sysLogDTO);
        });

    }

    /**
     * 异常通知
     *
     * @param joinPoint 端点
     * @param e         异常
     */
    @AfterThrowing(pointcut = "sysLogAspect()", throwing = "e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        tryCatch((aaa) -> {
            RequestLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (check(sysLog)) {
                return;
            }

            OptLogDTO optLogDTO = get();
            optLogDTO.setAbnormal(true);

            // 异常时强制记录参数
            if (sysLog.requestByError() && StrUtil.isEmpty(optLogDTO.getRequestParam())) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String params = getArgs(joinPoint.getArgs(), request);
                optLogDTO.setRequestParam(getText(params));
            }
            // 异常对象
            optLogDTO.setExceptionStack(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));

            publishEvent(optLogDTO);
        });
    }


    @NonNull
    private OptLogDTO buildOptLogDTO(JoinPoint joinPoint, RequestLog sysLog) {
        // 开始时间
        OptLogDTO optLogDTO = get();
        HttpServletRequest request = setParams(joinPoint, sysLog, optLogDTO);

        optLogDTO.setCreatedBy(ContextUtil.getUserId());
        optLogDTO.setStartTime(LocalDateTime.now());
        optLogDTO.setUserId(ContextUtil.getUserId());
        optLogDTO.setCreatedOrgId(ContextUtil.getCurrentCompanyId());
        optLogDTO.setToken(ContextUtil.getToken());
        optLogDTO.setLogType(sysLog.logType()); // 补全：日志类型（1查询/2新增/3修改/4删除/9其他）
        optLogDTO.setHttpThreadLocal(JSON.toJSONString(ContextUtil.getLocalMap()));
        // 类名
        optLogDTO.setClassPath(joinPoint.getTarget().getClass().getName());
        //获取执行的方法名
        optLogDTO.setMethodName(joinPoint.getSignature().getName());

        optLogDTO.setIpAddress(JakartaServletUtil.getClientIP(request));
        optLogDTO.setHttpUri(URLUtil.getPath(request.getRequestURI()));
        optLogDTO.setHttpMethod(request.getMethod());
        optLogDTO.setUa(StrUtil.sub(request.getHeader("user-agent"), 0, 500));

        fillLogDescription(joinPoint, sysLog, optLogDTO);

        if (StrUtil.isEmpty(optLogDTO.getTrace())) {
            optLogDTO.setTrace(StrUtil.blankToDefault(MDC.get(ContextConstants.TRACE), request.getHeader(ContextConstants.TRACE)));
        }
        return optLogDTO;
    }

    @NonNull
    private HttpServletRequest setParams(JoinPoint joinPoint, RequestLog sysLog, OptLogDTO optLogDTO) {
        // 参数
        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "只能在Spring Web环境使用@RequestLog记录日志")).getRequest();
        if (sysLog.request()) {
            String strArgs = getArgs(args, request);
            optLogDTO.setRequestParam(getText(strArgs));
        }
        return request;
    }

    private void fillLogDescription(JoinPoint joinPoint, RequestLog sysLog, OptLogDTO optLogDTO) {
        // 控制器模块描述（@Tag注解）
        String classDesc = Optional.ofNullable(joinPoint.getTarget().getClass().getAnnotation(Tag.class))
                .map(Tag::name)
                .orElse(StrUtil.EMPTY);
        // 方法操作描述
        String methodDesc = parseSpEl(joinPoint, sysLog.value());

        // 拼接描述
        if (sysLog.controllerApiValue() && StrUtil.isNotEmpty(classDesc)) {
            optLogDTO.setDescription(StrUtil.format("{}-{}", classDesc, methodDesc));
        } else {
            optLogDTO.setDescription(methodDesc);
        }

    }

    /**
     * 解析SpEL表达式
     */
    private String parseSpEl(JoinPoint joinPoint, String spEl) {
        if (StrUtil.isEmpty(spEl) || !spEl.contains(StrPool.HASH)) {
            return spEl;
        }
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
            if (Objects.isNull(paramNames) || paramNames.length == 0) {
                return spEl;
            }

            StandardEvaluationContext context = new StandardEvaluationContext();
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
                context.setVariable("p" + i, args[i]);
            }
            // 注入线程上下文参数
            ThreadLocalParam threadLocalParam = new ThreadLocalParam();
            BeanUtil.fillBeanWithMap(ContextUtil.getLocalMap(), threadLocalParam, true);
            context.setVariable("threadLocal", threadLocalParam);

            Expression expression = spelExpressionParser.parseExpression(spEl);
            Object value = expression.getValue(context);
            return Objects.isNull(value) ? spEl : value.toString();
        } catch (Exception e) {
            log.warn("SpEL表达式解析失败：{}", spEl, e);
            return spEl;
        }
    }


    private OptLogDTO get() {
        OptLogDTO sysLog = THREAD_LOCAL.get();
        if (sysLog == null) {
            return new OptLogDTO();
        }
        return sysLog;
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            log.warn("记录操作日志异常", e);
            THREAD_LOCAL.remove();
        }
    }

    private void publishEvent(OptLogDTO sysLog) {
        sysLog.setFinishTime(LocalDateTime.now());
        sysLog.setConsumingTime(sysLog.getStartTime().until(sysLog.getFinishTime(), ChronoUnit.MILLIS));
        SpringUtil.publishEvent(new SysLogEvent(sysLog));
        THREAD_LOCAL.remove();
    }

    /**
     * 监测是否需要记录日志
     *
     * @param requestLog    操作日志
     * @return true 表示不需要记录日志
     */
    private boolean check(RequestLog requestLog) {
        return Objects.isNull(requestLog) || !requestLog.enabled();
    }

    /**
     * 截取指定长度的字符串
     *
     * @param val 参数
     * @return 截取文本
     */
    private String getText(String val) {
        return StrUtil.sub(val, 0, 65535);
    }

    private String getArgs(Object[] args, HttpServletRequest request) {
        try {
            // 过滤掉请求/响应对象
            Object[] params = Arrays.stream(args)
                    .filter(item -> !(item instanceof ServletRequest || item instanceof ServletResponse))
                    .toArray();
            // 文件上传不序列化
            if (StrUtil.contains(request.getContentType(), FORM_DATA_CONTENT_TYPE)) {
                return StrUtil.EMPTY;
            }
            return JSON.toJSONString(params);
        } catch (Exception e) {
            log.warn("请求参数解析失败", e);
            return Arrays.toString(args);
        }
    }

}
