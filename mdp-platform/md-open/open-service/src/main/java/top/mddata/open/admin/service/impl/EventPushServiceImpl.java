package top.mddata.open.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.gitee.sop.support.exception.OpenException;
import com.gitee.sop.support.exception.SignException;
import com.gitee.sop.support.message.ApiResponse;
import com.gitee.sop.support.util.SignUtil;
import com.google.common.collect.Lists;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.base.utils.ArgumentAssert;
import top.mddata.base.util.StrPool;
import top.mddata.common.enumeration.StoryMessageEnum;
import top.mddata.open.admin.entity.AppKeys;
import top.mddata.open.admin.entity.EventPush;
import top.mddata.open.admin.entity.EventPushLog;
import top.mddata.open.admin.enumeration.EventTypeEnum;
import top.mddata.open.admin.enumeration.ExecStatusEnum;
import top.mddata.open.admin.mapper.EventPushMapper;
import top.mddata.open.admin.properties.NotifyProperties;
import top.mddata.open.admin.service.AppKeysService;
import top.mddata.open.admin.service.EventPushLogService;
import top.mddata.open.admin.service.EventPushService;
import top.mddata.open.admin.service.processor.MultiThreadTaskProcessor;
import top.mddata.open.admin.vo.AppKeysVo;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 事件推送任务 服务层实现。
 *
 * @author henhen6
 * @since 2026-01-12 21:28:36
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventPushServiceImpl extends SuperServiceImpl<EventPushMapper, EventPush> implements EventPushService {
    private final AppKeysService appKeysService;
    private final NotifyProperties notifyProperties;
    private final EventPushLogService eventPushLogService;
    private final MultiThreadTaskProcessor processor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByEventTrigger(String eventCode, Long triggerId, String requestData) {
        List<AppKeysVo> appKeysList = appKeysService.findByEventCode(eventCode);
        log.info("[事件触发] 事件类型： {}，订阅该事件的应用数量： {}", eventCode, appKeysList.size());

        List<EventPush> eventPushList = appKeysList.stream().map(appKeys -> {
            EventPush eventPush = new EventPush();
            eventPush.setEventCode(eventCode);
            eventPush.setEventTriggerId(triggerId);
            eventPush.setAppId(appKeys.getAppId());
            eventPush.setAppKey(appKeys.getAppKey());
            eventPush.setNotifyUrl(appKeys.getNotifyUrl());

            eventPush.setRequestData(requestData);
            eventPush.setMaxRequestCnt(notifyProperties.getMaxRetry());
            eventPush.setRequestCnt(0);
            eventPush.setExecStatus(ExecStatusEnum.WAIT.getCode());
            return eventPush;
        }).toList();

        super.saveBatch(eventPushList);

        execPushTask(eventPushList, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean end(Long id) {
        return updateChain().set(EventPush::getExecStatus, ExecStatusEnum.END.getCode())
                .eq(EventPush::getExecStatus, ExecStatusEnum.FAIL.getCode())
                .eq(EventPush::getId, id).update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long executeImmediately(Long id) {
        EventPush eventPush = getById(id);

        execPushTask(List.of(eventPush), true);
        return eventPush.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retry(LocalDateTime now) {
        ArgumentAssert.notNull(now, "当前时间不能为空");
        LocalDateTime nextTime = now.withSecond(0).withNano(0);
        List<EventPush> tasks = list(QueryWrapper.create().le(EventPush::getNextRequestTime, nextTime).eq(EventPush::getExecStatus, ExecStatusEnum.FAIL.getCode()));

        if (CollUtil.isEmpty(tasks)) {
            log.info("[{}]表无重试记录", EventPush.TABLE_NAME);
            return;
        }
        execPushTask(tasks, false);
    }

    public void execPushTask(List<EventPush> tasks, boolean immediately) {
        // 准备数据：创建N*M个任务列表
        List<List<MultiThreadTaskProcessor.Task>> allTasks = new ArrayList<>();

        int numberOfThreads = notifyProperties.getThreads(); // 使用x个线程
        int tasksPerList = notifyProperties.getThreadTasks(); // 每个list有x个任务
        log.info("[事件触发] 启用{}线程，每个线程有{}个任务", numberOfThreads, tasksPerList);

        // 每个线程处理 threadTasks 个任务
        List<List<EventPush>> partition = Lists.partition(tasks, tasksPerList);
        int totalLists = partition.size(); // 总共x个list
        log.info("[事件触发] 总共{}个重试任务, 拆分为{}, 每个list有{}个任务, 使用{}个线程", tasks.size(), totalLists, tasksPerList, numberOfThreads);

        // 初始化任务数据
        for (List<EventPush> subTasks : partition) {
            List<MultiThreadTaskProcessor.Task> list = new ArrayList<>();
            for (EventPush subTask : subTasks) {
                list.add(() -> push(subTask, immediately));
            }
            allTasks.add(list);
        }

        // 执行多线程处理
        processor.processTasksAsync(allTasks, numberOfThreads);
        log.info("任务已提交");
    }

    private void push(EventPush eventPush, boolean immediately) {
        try {
            log.info("[事件触发] 开始重试, notifyId={}", eventPush.getId());
            if (!immediately && Objects.equals(eventPush.getExecStatus(), ExecStatusEnum.RETRY_OVER.getCode())) {
                log.warn("[事件触发] 重试次数已用尽, notifyId={}", eventPush.getId());
                return;
            }
            // 发送请求
            doNotify(eventPush);
        } catch (SignException e) {
            log.error("[事件触发] 重试签名错误，notifyId={}", eventPush.getId(), e);
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "重试失败，签名错误");
        }
    }

    private Long doNotify(EventPush eventPush) throws SignException {
        eventPush.setRequestCnt(eventPush.getRequestCnt() + 1);
        eventPush.setLastRequestTime(LocalDateTime.now());
        eventPush.setNotifyUrl(eventPush.getNotifyUrl());

        EventPushLog eventPushLog = new EventPushLog();

        String notifyUrl = eventPush.getNotifyUrl();
        // 构建请求参数
        Map<String, String> params = buildParams(eventPush);
        if (StrUtil.isBlank(notifyUrl)) {
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "回调接口不能为空");
        }

        String json = JSON.toJSONString(params);
        eventPushLog.setRequestData(json);
        log.info("发送回调请求，notifyUrl={}, content={}", notifyUrl, json);
        eventPushLog.setRequestTime(LocalDateTime.now());

        try (HttpResponse responseResult = HttpRequest.post(notifyUrl).timeout(notifyProperties.getTimeout()).body(json).execute()) {
            eventPushLog.setResponseTime(LocalDateTime.now());
            String resultContent = responseResult.body();
            eventPushLog.setResponseData(resultContent);

            ApiResponse r = JSON.parseObject(resultContent, ApiResponse.class);
            if (ApiResponse.SUCCESS_CODE.equals(r.getCode())) {
                // 更新状态
                eventPush.setExecStatus(ExecStatusEnum.SUCCESS.getCode());
                eventPushLog.setErrorMsg(StrPool.EMPTY);
            } else {
                // 回调失败
                log.error("[事件触发] 返回数据格式异常 result={}, code= {},", resultContent, r.getCode());
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, resultContent);
            }

        } catch (Exception e) {
            log.error("[事件触发] 回调请求失败, notifyUrl={}, params={}, request={}", notifyUrl, params, eventPush, e);
            eventPushLog.setResponseTime(LocalDateTime.now());
            eventPush.setExecStatus(ExecStatusEnum.FAIL.getCode());
            if (e instanceof JSONException) {
                eventPushLog.setErrorMsg("返回值格式错误，无法解析为 ApiResponse.class，请返回正确格式： {\"code\": \"0\", \"msg\": \"\" }");
            } else if (e instanceof OpenException openEx) {
                eventPushLog.setErrorMsg(openEx.getSubMsg());
            } else {
                eventPushLog.setErrorMsg(e.getMessage());
            }
            LocalDateTime nextRequestTime = buildNextSendTime(eventPush.getRequestCnt());
            eventPush.setNextRequestTime(nextRequestTime);
            if (nextRequestTime == null) {
                log.error("回调请求次数达到上线, notifyUrl={}, params={}", notifyUrl, params);
                eventPush.setExecStatus(ExecStatusEnum.RETRY_OVER.getCode());
            }
        } finally {
            saveOrUpdate(eventPush);
            eventPushLog.setExecStatus(eventPush.getExecStatus());
            eventPushLog.setEventPushId(eventPush.getId());
            eventPushLogService.save(eventPushLog);
        }

        return eventPush.getId();
    }

    private Map<String, String> buildParams(EventPush eventPush) throws SignException {
        // 公共请求参数
        Map<String, String> params = new HashMap<>();
        params.put("app_key", eventPush.getAppKey());
        params.put("type", EventTypeEnum.EVENT_PUSH.name());
        params.put("format", "json");
        params.put("charset", "utf8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("method", eventPush.getEventCode());
        params.put("biz_content", eventPush.getRequestData());
        String content = SignUtil.getSignContent(params);
        AppKeys appKeys = appKeysService.getByAppId(eventPush.getAppId());
        if (appKeys != null) {
            String sign = SignUtil.rsa256Sign(content, appKeys.getPrivateKeyPlatform(), null);
            params.put("sign", sign);
        }

        return params;
    }

    /**
     * 构建下一次重试时间
     *
     * @param currentSendCnt 当前发送次数
     * @return 返回null表示重试次数用完
     */
    private LocalDateTime buildNextSendTime(Integer currentSendCnt) {
        String[] split = notifyProperties.getTimeLevel().split(",");
        if (currentSendCnt >= split.length) {
            return null;
        }
        // 1m
        String exp = split[currentSendCnt - 1];
        // 秒,毫秒归零
        LocalDateTime time = LocalDateTime.now().withSecond(0).withNano(0);
        // 最后一个字符，如：m,h,d
        char ch = exp.charAt(exp.length() - 1);
        int value = NumberUtils.toInt(exp.substring(0, exp.length() - 1));
        return switch (String.valueOf(ch).toLowerCase()) {
            case "m" -> time.plusMinutes(value);
            case "h" -> time.plusHours(value);
            case "d" -> time.plusDays(value);
            default -> null;
        };
    }
}
