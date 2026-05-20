package top.mddata.console.facade.impl.system;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.common.constant.EchoApi;
import top.mddata.console.facade.api.system.DictApi;
import top.mddata.console.facade.system.DictFacade;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 字典实现
 * @author henhen6
 * @since 2024/9/20 23:29
 */
@Service(EchoApi.DICT_CLASS)
@RequiredArgsConstructor
public class DictFacadeImpl implements DictFacade {
    private final DictApi dictApi;

    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return dictApi.findByIds(ids);
    }
}
