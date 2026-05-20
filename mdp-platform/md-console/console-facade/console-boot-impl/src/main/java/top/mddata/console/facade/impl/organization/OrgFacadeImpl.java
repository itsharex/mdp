package top.mddata.console.facade.impl.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.console.facade.organization.OrgFacade;
import top.mddata.console.service.organization.OrgService;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author henhen6
 * @since 2025/9/16 12:23
 */
@Service
@RequiredArgsConstructor
public class OrgFacadeImpl implements OrgFacade {
    private final OrgService orgService;

    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return orgService.findByIds(ids);
    }
}
