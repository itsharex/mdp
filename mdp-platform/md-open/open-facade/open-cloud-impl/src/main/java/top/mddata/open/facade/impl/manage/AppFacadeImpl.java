package top.mddata.open.facade.impl.manage;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mddata.base.base.R;
import top.mddata.open.vo.admin.AppVo;
import top.mddata.open.api.manage.AppApi;
import top.mddata.open.manage.facade.AppFacade;

import java.util.List;

/**
 * 应用管理单体版实现类
 *
 * @author henhen6
 * @since 2025/8/12 11:28
 */
@Service
@RequiredArgsConstructor
public class AppFacadeImpl implements AppFacade {
    private final AppApi appApi;

    @Override
    public R<List<AppVo>> listNeedPushApp() {
        return appApi.listNeedPushApp();
    }


    @Override
    public R<AppVo> getAppByAppKey(String key) {
        return appApi.getAppByAppKey(key);
    }
}
