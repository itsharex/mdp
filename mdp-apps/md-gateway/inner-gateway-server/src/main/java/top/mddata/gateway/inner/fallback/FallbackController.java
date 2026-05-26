package top.mddata.gateway.inner.fallback;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.mddata.base.base.R;
import top.mddata.base.exception.code.ExceptionCode;

/**
 * 响应超时熔断处理器
 *
 * @author henhen
 */
@RestController
@Slf4j
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<R> fallback(ServerWebExchange exchange) {
        return Mono.just(R.fail(ExceptionCode.SYSTEM_TIMEOUT.getCode(), ExceptionCode.SYSTEM_TIMEOUT.getMsg()));
    }
}
