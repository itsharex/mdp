package top.mddata.gateway.sop.exception;


import top.mddata.gateway.sop.request.ApiRequestContext;
import com.gitee.sop.support.message.ApiResponse;

/**
 * @author 六如
 */
public interface ExceptionExecutor {

    ApiResponse executeException(ApiRequestContext apiRequestContext, Exception e);

}
