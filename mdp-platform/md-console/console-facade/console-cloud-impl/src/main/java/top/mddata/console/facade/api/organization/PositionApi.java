package top.mddata.console.facade.api.organization;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.mddata.common.constant.AppConstants;
import top.mddata.console.facade.api.organization.fallback.PositionApiFallback;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author henhen
 * @since 2026/5/10 23:07
 */
@FeignClient(name = AppConstants.CONSOLE_SERVER, fallback = PositionApiFallback.class)
public interface PositionApi {
    /**
     * 根据id查询待回显参数
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return
     */
    @PostMapping("/organization/position/findByIds")
    Map<Serializable, Object> findByIds(@RequestParam(value = "ids") Set<Serializable> ids);
}
