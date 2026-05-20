package top.mddata.open.api.manage;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.mddata.base.base.R;
import top.mddata.common.constant.AppConstants;
import top.mddata.open.vo.admin.AppVo;

import java.util.List;

/**
 * 应用接口
 *
 * @author henhen6
 * @since 2025/8/12 11:24
 */
@FeignClient(name = AppConstants.OPEN_SERVER, path = "/admin/app")
public interface AppApi {
    /**
     * 查询需要 接收事件推送的应用
     *
     * @return 应用
     */
    @GetMapping("/listNeedPushApp")
    R<List<AppVo>> listNeedPushApp();

    /**
     * 根据id查询应用
     *
     * @param appKey 应用标识
     * @return 应用
     */
    @GetMapping("/getAppByAppKey")
    R<AppVo> getAppByAppKey(@RequestParam String appKey);

}
