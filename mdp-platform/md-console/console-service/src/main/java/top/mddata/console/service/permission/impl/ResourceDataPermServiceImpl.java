package top.mddata.console.service.permission.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.console.entity.permission.ResourceDataPerm;
import top.mddata.console.mapper.permission.ResourceDataPermMapper;
import top.mddata.console.service.permission.ResourceDataPermService;

/**
 * 数据权限 服务层实现。
 *
 * @author henhen6
 * @since 2025-11-12 16:27:16
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceDataPermServiceImpl extends SuperServiceImpl<ResourceDataPermMapper, ResourceDataPerm> implements ResourceDataPermService {

}
