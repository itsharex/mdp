package top.mddata.console.facade.api.system.fallback;

import org.springframework.stereotype.Component;
import top.mddata.console.facade.api.system.DictApi;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author henhen
 * @since 2026/5/10 23:20
 */
@Component
public class DictApiFallback implements DictApi {
    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return Map.of();
    }
}
