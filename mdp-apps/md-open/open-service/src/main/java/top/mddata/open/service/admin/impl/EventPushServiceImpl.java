package top.mddata.open.service.admin.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.gitee.sop.support.exception.OpenException;
import com.gitee.sop.support.message.ApiResponse;
import com.google.common.collect.Lists;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.base.util.StrPool;
import top.mddata.base.utils.ArgumentAssert;
import top.mddata.common.enumeration.StoryMessageEnum;
import top.mddata.open.entity.admin.EventPush;
import top.mddata.open.entity.admin.EventPushLog;
import top.mddata.open.enumeration.admin.EventTypeEnum;
import top.mddata.open.enumeration.admin.ExecStatusEnum;
import top.mddata.open.enumeration.admin.NotifyEncryptionTypeEnum;
import top.mddata.open.mapper.admin.EventPushMapper;
import top.mddata.open.service.admin.AppKeysService;
import top.mddata.open.service.admin.EventPushLogService;
import top.mddata.open.service.admin.EventPushService;
import top.mddata.open.service.admin.processor.MultiThreadTaskProcessor;
import top.mddata.open.service.admin.properties.NotifyProperties;
import com.gitee.sop.support.util.NotifyPushUtil;
import top.mddata.open.vo.admin.AppKeysVo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        log.info("[事件触发] 事件类型：{}，订阅该事件的应用数量：{}", eventCode, appKeysList.size());

        List<EventPush> eventPushList = appKeysList.stream().map(appKeys -> {
            EventPush eventPush = new EventPush();
            eventPush.setEventCode(eventCode);
            eventPush.setEventTriggerId(triggerId);
            eventPush.setAppId(appKeys.getAppId());
            eventPush.setAppKey(appKeys.getAppKey());
            eventPush.setNotifyUrl(appKeys.getNotifyUrl());
            // 冗余加密配置，避免推送时再查库
            eventPush.setNotifyEncryptionType(appKeys.getNotifyEncryptionType());
            eventPush.setNotifyToken(appKeys.getNotifyToken());
            eventPush.setNotifyEncodingAesKey(appKeys.getNotifyEncodingAesKey());

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
        List<List<MultiThreadTaskProcessor.Task>> allTasks = new ArrayList<>();

        int numberOfThreads = notifyProperties.getThreads();
        int tasksPerList = notifyProperties.getThreadTasks();
        log.info("[事件触发] 启用{}线程，每个线程有{}个任务", numberOfThreads, tasksPerList);

        List<List<EventPush>> partition = Lists.partition(tasks, tasksPerList);
        int totalLists = partition.size();
        log.info("[事件触发] 总共{}个重试任务, 拆分为{}, 每个list有{}个任务, 使用{}个线程", tasks.size(), totalLists, tasksPerList, numberOfThreads);

        for (List<EventPush> subTasks : partition) {
            List<MultiThreadTaskProcessor.Task> list = new ArrayList<>();
            for (EventPush subTask : subTasks) {
                list.add(() -> push(subTask, immediately));
            }
            allTasks.add(list);
        }

        processor.processTasksAsync(allTasks, numberOfThreads);
        log.info("任务已提交");
    }

    private void push(EventPush eventPush, boolean immediately) {
        try {
            log.info("[事件触发] 开始推送, notifyId={}", eventPush.getId());
            if (!immediately && Objects.equals(eventPush.getExecStatus(), ExecStatusEnum.RETRY_OVER.getCode())) {
                log.warn("[事件触发] 重试次数已用尽, notifyId={}", eventPush.getId());
                return;
            }
            doNotify(eventPush);
        } catch (Exception e) {
            log.error("[事件触发] 推送异常，notifyId={}", eventPush.getId(), e);
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "推送失败：" + e.getMessage());
        }
    }

    /**
     * 执行推送，根据加密模式构建不同的请求体和签名
     */
    private Long doNotify(EventPush eventPush) {
        eventPush.setRequestCnt(eventPush.getRequestCnt() + 1);
        eventPush.setLastRequestTime(LocalDateTime.now());

        EventPushLog eventPushLog = new EventPushLog();
        String notifyUrl = eventPush.getNotifyUrl();

        if (StrUtil.isBlank(notifyUrl)) {
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "回调接口不能为空");
        }

        // 构建推送数据
        String timestamp = NotifyPushUtil.generateTimestamp();
        String nonce = NotifyPushUtil.generateNonce();
        String appKey = eventPush.getAppKey();

        // 业务数据
        JSONObject bizData = new JSONObject();
        bizData.put("type", EventTypeEnum.EVENT_PUSH.name());
        bizData.put("method", eventPush.getEventCode());
        bizData.put("app_key", eventPush.getAppKey());
        bizData.put("timestamp", timestamp);
        bizData.put("event_trigger_id", eventPush.getEventTriggerId());
        bizData.put("biz_content", eventPush.getRequestData());
        String plaintext = bizData.toJSONString();

        // 根据加密模式构建请求体和URL
        Integer encryptionType = eventPush.getNotifyEncryptionType();
        if (encryptionType == null) {
            encryptionType = NotifyEncryptionTypeEnum.PLAINTEXT.getCode();
        }

        String requestUrl;
        String requestBody;

        if (Objects.equals(encryptionType, NotifyEncryptionTypeEnum.PLAINTEXT.getCode())) {
            // 明文模式：直接发送明文，signature = sha1(token, timestamp, nonce)
            requestBody = plaintext;
            String signature = NotifyPushUtil.calcSignature(
                    eventPush.getNotifyToken(), timestamp, nonce, StrPool.EMPTY);
            requestUrl = appendUrlParams(notifyUrl, signature, null, timestamp, nonce, null);

        } else if (Objects.equals(encryptionType, NotifyEncryptionTypeEnum.COMPATIBLE.getCode())) {
            // 兼容模式：明文+密文共存
            String encrypt = NotifyPushUtil.encrypt(plaintext, eventPush.getNotifyEncodingAesKey(), appKey);
            String msgSignature = NotifyPushUtil.calcSignature(
                    eventPush.getNotifyToken(), timestamp, nonce, encrypt);
            JSONObject body = NotifyPushUtil.buildCompatibleBody(plaintext, encrypt, appKey);
            requestBody = body.toJSONString();
            requestUrl = appendUrlParams(notifyUrl, null, msgSignature, timestamp, nonce, "aes");

        } else {
            // 安全模式：纯密文
            String encrypt = NotifyPushUtil.encrypt(plaintext, eventPush.getNotifyEncodingAesKey(), appKey);
            String msgSignature = NotifyPushUtil.calcSignature(
                    eventPush.getNotifyToken(), timestamp, nonce, encrypt);
            JSONObject body = NotifyPushUtil.buildEncryptedBody(encrypt, appKey);
            requestBody = body.toJSONString();
            requestUrl = appendUrlParams(notifyUrl, null, msgSignature, timestamp, nonce, "aes");
        }

        eventPushLog.setRequestData(requestBody);
        log.info("发送事件推送请求，url={}, content={}", requestUrl, requestBody);
        eventPushLog.setRequestTime(LocalDateTime.now());

        try (HttpResponse responseResult = HttpRequest.post(requestUrl)
                .timeout(notifyProperties.getTimeout())
                .body(requestBody)
                .execute()) {
            eventPushLog.setResponseTime(LocalDateTime.now());
            String resultContent = responseResult.body();
            eventPushLog.setResponseData(resultContent);

            ApiResponse r = JSON.parseObject(resultContent, ApiResponse.class);
            if (ApiResponse.SUCCESS_CODE.equals(r.getCode())) {
                eventPush.setExecStatus(ExecStatusEnum.SUCCESS.getCode());
                eventPushLog.setErrorMsg(StrPool.EMPTY);
            } else {
                log.error("[事件触发] 返回数据异常 result={}, code={}", resultContent, r.getCode());
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, resultContent);
            }

        } catch (Exception e) {
            log.error("[事件触发] 回调请求失败, notifyUrl={}, eventPushId={}", notifyUrl, eventPush.getId(), e);
            eventPushLog.setResponseTime(LocalDateTime.now());
            eventPush.setExecStatus(ExecStatusEnum.FAIL.getCode());
            if (e instanceof JSONException) {
                eventPushLog.setErrorMsg("返回值格式错误，无法解析为 ApiResponse.class，请返回正确格式：{\"code\": 0, \"msg\": \"\"}");
            } else if (e instanceof OpenException openEx) {
                eventPushLog.setErrorMsg(openEx.getSubMsg());
            } else {
                eventPushLog.setErrorMsg(e.getMessage());
            }
            LocalDateTime nextRequestTime = buildNextSendTime(eventPush.getRequestCnt());
            eventPush.setNextRequestTime(nextRequestTime);
            if (nextRequestTime == null) {
                log.error("回调请求次数达到上限, notifyUrl={}, eventPushId={}", notifyUrl, eventPush.getId());
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

    /**
     * 拼接URL参数
     */
    private String appendUrlParams(String baseUrl, String signature, String msgSignature,
                                   String timestamp, String nonce, String encryptType) {
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(baseUrl.contains("?") ? "&" : "?");
        if (StrUtil.isNotBlank(signature)) {
            sb.append("signature=").append(signature).append("&");
        }
        if (StrUtil.isNotBlank(msgSignature)) {
            sb.append("msg_signature=").append(msgSignature).append("&");
        }
        sb.append("timestamp=").append(timestamp).append("&");
        sb.append("nonce=").append(nonce);
        if (StrUtil.isNotBlank(encryptType)) {
            sb.append("&encrypt_type=").append(encryptType);
        }
        return sb.toString();
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
        String exp = split[currentSendCnt - 1];
        LocalDateTime time = LocalDateTime.now().withSecond(0).withNano(0);
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
