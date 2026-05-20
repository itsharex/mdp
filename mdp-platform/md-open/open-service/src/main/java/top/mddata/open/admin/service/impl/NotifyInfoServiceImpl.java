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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.base.utils.ArgumentAssert;
import top.mddata.base.util.StrPool;
import top.mddata.common.enumeration.StoryMessageEnum;
import top.mddata.open.dto.admin.NotifyInfoDto;
import top.mddata.open.entity.admin.AppKeys;
import top.mddata.open.entity.admin.NotifyInfo;
import top.mddata.open.entity.admin.NotifyInfoLog;
import top.mddata.open.enumeration.admin.ExecStatusEnum;
import top.mddata.open.admin.mapper.NotifyInfoMapper;
import top.mddata.open.admin.properties.NotifyProperties;
import top.mddata.open.admin.service.AppKeysService;
import top.mddata.open.admin.service.NotifyInfoLogService;
import top.mddata.open.admin.service.NotifyInfoService;
import top.mddata.open.admin.service.processor.MultiThreadTaskProcessor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 回调任务 服务层实现。
 *
 * @author henhen6
 * @since 2026-01-02 10:11:40
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(NotifyProperties.class)
public class NotifyInfoServiceImpl extends SuperServiceImpl<NotifyInfoMapper, NotifyInfo> implements NotifyInfoService {
    private final NotifyInfoLogService notifyInfoLogService;
    private final AppKeysService appKeysService;
    private final NotifyProperties notifyProperties;
    private final MultiThreadTaskProcessor processor;

    @Override
    public Boolean push(Long id, String url) {
        if (StrUtil.isNotBlank(url)) {
            NotifyInfo entity = new NotifyInfo();
            entity.setNotifyUrl(url).setId(id);
            updateById(entity);
        }
        Long notifyId = notifyImmediately(id);
        log.info("重新推送结果, notifyId={}", notifyId);
        return true;
    }

    @Override
    public Boolean end(Long id) {
        return updateChain().set(NotifyInfo::getExecStatus, ExecStatusEnum.END.getCode())
                .eq(NotifyInfo::getExecStatus, ExecStatusEnum.FAIL.getCode())
                .eq(NotifyInfo::getId, id).update();
    }

    @Override
    public void retry(LocalDateTime now) {
        ArgumentAssert.notNull(now, "当前时间不能为空");
        LocalDateTime nextTime = now.withSecond(0).withNano(0);
        List<NotifyInfo> tasks = list(QueryWrapper.create().le(NotifyInfo::getNextRequestTime, nextTime).eq(NotifyInfo::getExecStatus, ExecStatusEnum.FAIL.getCode()));

        if (CollUtil.isEmpty(tasks)) {
            log.info("[{}]表无重试记录", NotifyInfo.TABLE_NAME);
            return;
        }

        MultiThreadTaskProcessor processor = new MultiThreadTaskProcessor();
        // 准备数据：创建N*M个任务列表
        List<List<MultiThreadTaskProcessor.Task>> allTasks = new ArrayList<>();

        int numberOfThreads = notifyProperties.getThreads(); // 使用x个线程
        int tasksPerList = notifyProperties.getThreadTasks(); // 每个list有x个任务
        log.info("启用{}线程，每个线程有{}个任务", numberOfThreads, tasksPerList);

        // 每个线程处理 threadTasks 个任务
        List<List<NotifyInfo>> partition = Lists.partition(tasks, tasksPerList);
        int totalLists = partition.size(); // 总共x个list
        log.info("总共{}个重试任务, 拆分为{}, 每个list有{}个任务, 使用{}个线程", tasks.size(), totalLists, tasksPerList, numberOfThreads);

        // 初始化任务数据
        for (List<NotifyInfo> subTasks : partition) {
            List<MultiThreadTaskProcessor.Task> list = new ArrayList<>();
            for (NotifyInfo subTask : subTasks) {
                list.add(() -> {
                    retry(subTask);
                });
            }
            allTasks.add(list);
        }

        // 执行多线程处理
        processor.processTasksAsync(allTasks, numberOfThreads);
        log.info("任务已提交");
    }


    private void retry(NotifyInfo notifyInfo) {
        String content = notifyInfo.getRequestData();
        NotifyInfoDto notifyBO = JSON.parseObject(content, NotifyInfoDto.class);
        try {
            log.info("[notify]开始重试, notifyId={}", notifyInfo.getId());
            if (Objects.equals(notifyInfo.getExecStatus(), ExecStatusEnum.RETRY_OVER.getCode())) {
                log.warn("重试次数已用尽, notifyId={}", notifyInfo.getId());
                return;
            }
            // 发送请求
            doNotify(notifyBO, notifyInfo);
        } catch (SignException e) {
            log.error("[notify]重试签名错误，notifyId={}", notifyInfo.getId(), e);
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "重试失败，签名错误");
        }
    }


    @Override
    public R<Long> notify(NotifyInfoDto request) {
        try {
            NotifyInfo notifyInfo = buildRecord(request);
            return R.success(doNotify(request, notifyInfo));
        } catch (SignException e) {
            log.error("回调异常，服务端签名失败, request={}", request, e);
            return R.fail(e.getMessage());
        }
    }

    @Override
    public Long notifyImmediately(Long notifyId) {
        NotifyInfo notifyInfo = getById(notifyId);
        String content = notifyInfo.getRequestData();
        NotifyInfoDto notifyBO = JSON.parseObject(content, NotifyInfoDto.class);
        // 发送请求
        try {
            return doNotify(notifyBO, notifyInfo);
        } catch (SignException e) {
            log.error("回调异常，服务端签名失败, notifyId={}", notifyId, e);
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "回调失败，签名错误");
        }
    }

    private NotifyInfo buildRecord(NotifyInfoDto request) {
        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setCallLogId(request.getCallLogId());
        notifyInfo.setAppId(request.getAppId());
        notifyInfo.setAppKey(request.getAppKey());
        notifyInfo.setApiName(request.getApiName());
        notifyInfo.setApiVersion(request.getApiVersion());
        notifyInfo.setNotifyUrl(request.getNotifyUrl());
        notifyInfo.setRequestData(JSON.toJSONString(request));
        notifyInfo.setRequestCnt(0);
        notifyInfo.setMaxRequestCnt(notifyProperties.getMaxRetry());
        notifyInfo.setExecStatus(ExecStatusEnum.WAIT.getCode());
        notifyInfo.setRemark(request.getRemark());

        return notifyInfo;
    }

    private Long doNotify(NotifyInfoDto request, NotifyInfo notifyInfo) throws SignException {
        notifyInfo.setRequestCnt(notifyInfo.getRequestCnt() + 1);
        notifyInfo.setLastRequestTime(LocalDateTime.now());
        notifyInfo.setNotifyUrl(buildNotifyUrl(request, notifyInfo));

        NotifyInfoLog notifyLog = new NotifyInfoLog();

        String notifyUrl = notifyInfo.getNotifyUrl();
        // 构建请求参数
        Map<String, String> params = buildParams(request);
        if (StrUtil.isBlank(notifyUrl)) {
            throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, "回调接口不能为空");
        }

        String json = JSON.toJSONString(params);
        notifyLog.setRequestData(json);
        log.info("发送回调请求，notifyUrl={}, content={}", notifyUrl, json);
        notifyLog.setRequestTime(LocalDateTime.now());
        try (HttpResponse responseResult = HttpRequest.post(notifyUrl).timeout(notifyProperties.getTimeout()).body(json).execute()) {
            notifyLog.setResponseTime(LocalDateTime.now());

            String resultContent = responseResult.body();
            notifyLog.setResponseData(resultContent);

            // 返回值一定是JSON格式 {code: 0, msg: ""}
            ApiResponse r = JSON.parseObject(resultContent, ApiResponse.class);
            if (ApiResponse.SUCCESS_CODE.equals(r.getCode())) {
                // 更新状态
                notifyInfo.setExecStatus(ExecStatusEnum.SUCCESS.getCode());
                notifyLog.setErrorMsg(StrPool.EMPTY);
            } else {
                // 回调失败
                log.error("返回数据格式异常 result={}, code= {},", resultContent, r.getCode());
                throw new OpenException(StoryMessageEnum.PARAM_VALIDATION, resultContent);
            }
        } catch (Exception e) {
            notifyLog.setResponseTime(LocalDateTime.now());
            log.error("回调请求失败, notifyUrl={}, params={}, request={}", notifyUrl, params, request, e);
            notifyInfo.setExecStatus(ExecStatusEnum.FAIL.getCode());
            if (e instanceof JSONException) {
                notifyLog.setErrorMsg("返回值格式错误，无法解析为 ApiResponse.class，请返回正确格式： {\"code\": \"0\", \"msg\": \"\" }");
            } else if (e instanceof OpenException openEx) {
                notifyLog.setErrorMsg(openEx.getSubMsg());
            } else {
                notifyLog.setErrorMsg(e.getMessage());
            }

            LocalDateTime nextRequestTime = buildNextSendTime(notifyInfo.getRequestCnt());
            notifyInfo.setNextRequestTime(nextRequestTime);

            if (nextRequestTime == null) {
                log.error("回调请求次数达到上线, notifyUrl={}, params={}", notifyUrl, params);
                notifyInfo.setExecStatus(ExecStatusEnum.RETRY_OVER.getCode());
            }
        } finally {
            saveOrUpdate(notifyInfo);
            notifyLog.setExecStatus(notifyInfo.getExecStatus());
            notifyLog.setNotifyInfoId(notifyInfo.getId());
            notifyInfoLogService.save(notifyLog);
        }

        return notifyInfo.getId();
    }


    private Map<String, String> buildParams(NotifyInfoDto request) throws SignException {
        // 公共请求参数
        Map<String, String> params = new HashMap<>();
        String appKey = request.getAppKey();
        params.put("app_key", appKey);
        params.put("method", request.getApiName());
        params.put("format", "json");
        params.put("charset", request.getCharset());
        params.put("sign_type", "RSA2");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("version", request.getApiVersion());
        // 业务参数
        Map<String, Object> bizContent = request.getBizParams();
        params.put("biz_content", JSON.toJSONString(bizContent));
        String content = SignUtil.getSignContent(params);
        AppKeys appKeys = appKeysService.getByAppId(request.getAppId());
        if (appKeys != null) {
            String sign = SignUtil.rsa256Sign(content, appKeys.getPrivateKeyPlatform(), request.getCharset());
            params.put("sign", sign);
        }

        return params;
    }

    private String buildNotifyUrl(NotifyInfoDto request, NotifyInfo notifyInfo) {
        String savedUrl = notifyInfo.getNotifyUrl();
        if (StrUtil.isNotBlank(savedUrl)) {
            return savedUrl;
        }
        String notifyUrl = request.getNotifyUrl();
        if (StrUtil.isBlank(notifyUrl)) {
            AppKeys appKeys = appKeysService.getByAppId(request.getAppId());
            notifyUrl = appKeys != null ? appKeys.getNotifyUrl() : null;
        }
        return notifyUrl;
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
