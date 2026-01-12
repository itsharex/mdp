package top.mddata.gateway.sop.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import top.mddata.gateway.sop.request.ApiRequestContext;
import top.mddata.open.admin.entity.ApiCallLog;

/**
 * @author 六如
 */
@Getter
@AllArgsConstructor
public class RouteContext {

    private ApiRequestContext apiRequestContext;
    private ApiDto api;
    private AppDto app;
    @Setter
    private ApiCallLog apiCallLog;

}
