package top.mddata.open.facade.admin;

import top.mddata.base.base.R;
import top.mddata.open.vo.admin.AppVo;

import java.util.List;

/**
 * 应用接口
 *
 * @author henhen6
 * @since 2025/8/12 11:24
 */
public interface AppFacade {
    /**
     * 查询需要 接收事件推送的应用
     *
     * @return 应用
     */
    R<List<AppVo>> listNeedPushApp();



    /**
     * 根据id查询应用
     *
     * @param appKey 应用标识
     * @return 应用
     */
    R<AppVo> getAppByAppKey(String appKey);

}
