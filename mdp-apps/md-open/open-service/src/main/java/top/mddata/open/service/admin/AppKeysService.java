package top.mddata.open.service.admin;

import com.gitee.sop.support.util.RsaTool;
import top.mddata.base.mvcflex.service.SuperService;
import top.mddata.open.dto.admin.AppKeysDto;
import top.mddata.open.dto.client.AppEventSubscriptionDto;
import top.mddata.open.dto.client.AppKeysUpdateDto;
import top.mddata.open.entity.admin.AppKeys;
import top.mddata.open.vo.admin.AppKeysVo;

import java.util.List;

/**
 * 应用秘钥 服务层。
 *
 * @author henhen6
 * @since 2025-11-20 16:31:25
 */
public interface AppKeysService extends SuperService<AppKeys> {
    /**
     * 通过应用ID查询 秘钥
     *
     * @param appId 应用ID
     * @return 秘钥
     */
    AppKeys getByAppId(Long appId);

    /**
     * 通过appKey查询应用秘钥（包含通知加密配置）
     *
     * @param appKey 应用标识
     * @return 秘钥VO，不存在返回null
     */
    AppKeysVo getByAppKey(String appKey);

    /**
     * 查询订阅了指定事件的应用列表
     *
     * @param eventCode 事件类型
     * @return 应用
     */
    List<AppKeysVo> findByEventCode(String eventCode);

    /**
     * 获取秘钥信息（含通知加密配置）
     *
     * @param appId 应用ID
     * @return 秘钥VO
     */
    AppKeysVo getKeys(Long appId);

    /**
     * 修改应用秘钥信息（开发者更新通知配置）
     *
     * @param param 通知配置参数
     * @return 应用秘钥实体
     */
    AppKeys updateKeysByClient(AppKeysUpdateDto param);

    /**
     * 修改应用秘钥信息（管理员）
     *
     * @param param 秘钥参数
     * @return 应用秘钥实体
     */
    AppKeys updateAppKeys(AppKeysDto param);

    /**
     * 修改应用事件订阅
     *
     * @param param 应用事件参数
     * @return 应用id
     */
    Long updateEventSubscription(AppEventSubscriptionDto param);

    /**
     * 重置应用秘钥
     * @param appId 应用ID
     * @param keyFormat 秘钥格式
     * @return 秘钥
     * @throws Exception 异常
     */
    RsaTool.KeyStore resetAppKeys(Long appId, Integer keyFormat) throws Exception;

    /**
     * 创建应用秘钥
     *
     * @param keyFormat 秘钥格式
     * @return 秘钥
     * @throws Exception 异常
     */
    RsaTool.KeyStore createKeys(Integer keyFormat) throws Exception;
}
