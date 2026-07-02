package top.mddata.sdk.simple.api.token;

import top.mddata.sdk.core.param.BaseParam;
import top.mddata.sdk.simple.request.token.AccessTokenGetDto;
import top.mddata.sdk.simple.response.token.AccessTokenGetResp;

/**
 * 获取访问令牌接口
 * <p>
 * 通过 appKey + appSecret 换取 accessToken，
 * 后续使用 accessToken 调用其他需要认证的接口。
 *
 * @author henhen
 * @since 2026/7/2
 */
public class AccessTokenGetApi extends BaseParam<AccessTokenGetDto, AccessTokenGetResp> {

    @Override
    protected String method() {
        return "accessToken.get";
    }

    @Override
    public Boolean getSignEnabled() {
        return true;
    }
}
