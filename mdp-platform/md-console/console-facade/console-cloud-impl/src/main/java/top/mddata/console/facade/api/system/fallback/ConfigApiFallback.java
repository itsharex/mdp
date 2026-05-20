package top.mddata.console.facade.api.system.fallback;

import org.springframework.stereotype.Component;
import top.mddata.base.base.R;
import top.mddata.console.facade.api.system.ConfigApi;
import top.mddata.console.vo.system.ConfigVo;

/**
 *
 * @author henhen
 * @since 2026/5/10 23:20
 */
@Component
public class ConfigApiFallback implements ConfigApi {
    @Override
    public R<ConfigVo> getConfig(String uniqKey) {
        return R.timeout();
    }

    @Override
    public R<Long> getLong(String uniqKey, Long defaultValue) {
        return R.timeout();
    }

    @Override
    public R<Integer> getInteger(String uniqKey, Integer defaultValue) {
        return R.timeout();
    }

    @Override
    public R<String> getString(String uniqKey, String defaultValue) {
        return R.timeout();
    }

    @Override
    public R<Boolean> getBoolean(String uniqKey, Boolean defaultValue) {
        return R.timeout();
    }
}
