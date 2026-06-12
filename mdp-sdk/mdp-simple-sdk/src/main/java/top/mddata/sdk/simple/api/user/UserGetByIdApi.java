package top.mddata.sdk.simple.api.user;


import top.mddata.sdk.core.param.BaseParam;
import top.mddata.sdk.core.param.IdRequest;
import top.mddata.sdk.simple.response.user.UserResp;

/**
 * 获取用户信息
 * @author henhen
 */
public class UserGetByIdApi extends BaseParam<IdRequest, UserResp> {
    @Override
    protected String method() {
        return "user.getById";
    }
}