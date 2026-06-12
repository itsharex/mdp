package top.mddata.sdk.simple.api.org;


import top.mddata.sdk.core.param.BaseParam;
import top.mddata.sdk.core.param.IdRequest;
import top.mddata.sdk.simple.response.org.OrgResp;

/**
 * 获取用户信息
 * @author henhen
 */
public class OrgGetByIdApi extends BaseParam<IdRequest, OrgResp> {
    @Override
    protected String method() {
        return "org.getById";
    }
}