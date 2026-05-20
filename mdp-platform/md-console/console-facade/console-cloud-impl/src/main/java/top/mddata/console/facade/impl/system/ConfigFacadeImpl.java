package top.mddata.console.facade.impl.system;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.console.facade.api.system.ConfigApi;
import top.mddata.console.system.facade.ConfigFacade;
import top.mddata.console.vo.system.ConfigVo;

/**
 *
 * @author henhen6
 * @since 2025/10/10 23:55
 */
@Service
@RequiredArgsConstructor
public class ConfigFacadeImpl implements ConfigFacade {
    private final ConfigApi configApi;

    @Override
    public ConfigVo getConfig(String uniqKey) {
        R<ConfigVo> result = configApi.getConfig(uniqKey);
        return result.getIsSuccess() ? result.getData() : null;
    }

    @Override
    public Long getLong(String uniqKey, Long defaultValue) {
        R<Long> result = configApi.getLong(uniqKey, defaultValue);
        return result.getIsSuccess() ? result.getData() : null;
    }

    @Override
    public Integer getInteger(String uniqKey, Integer defaultValue) {
        R<Integer> result = configApi.getInteger(uniqKey, defaultValue);
        return result.getIsSuccess() ? result.getData() : null;
    }

    @Override
    public String getString(String uniqKey, String defaultValue) {
        R<String> result = configApi.getString(uniqKey, defaultValue);
        return result.getIsSuccess() ? result.getData() : null;
    }

    @Override
    public Boolean getBoolean(String uniqKey, Boolean defaultValue) {
        R<Boolean> result = configApi.getBoolean(uniqKey, defaultValue);
        return result.getIsSuccess() ? result.getData() : false;
    }
}
