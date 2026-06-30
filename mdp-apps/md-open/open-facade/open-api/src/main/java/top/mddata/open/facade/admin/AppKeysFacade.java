package top.mddata.open.facade.admin;

import top.mddata.base.base.R;
import top.mddata.open.vo.admin.AppKeysVo;

/**
 * 应用秘钥接口
 *
 * @author henhen6
 * @since 2026/06/29
 */
public interface AppKeysFacade {

    /**
     * 根据appKey查询应用秘钥（包含通知加密配置）
     *
     * @param appKey 应用标识
     * @return 秘钥VO
     */
    R<AppKeysVo> getByAppKey(String appKey);
}
