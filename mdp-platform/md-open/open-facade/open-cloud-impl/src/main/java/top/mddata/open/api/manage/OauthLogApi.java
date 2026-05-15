package top.mddata.open.api.manage;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.mddata.common.constant.AppConstants;
import top.mddata.open.admin.dto.OauthLogDto;

/**
 * 授权记录
 *
 * @author henhen6
 * @since 2025/8/21 23:33
 */
@FeignClient(name = AppConstants.OPEN_SERVER)
public interface OauthLogApi {
    /**
     * 保存授权记录
     *
     * @param dlt 记录
     */
    @PostMapping("/admin/oauthLog/save")
    void save(@RequestBody OauthLogDto dlt);
}
