package top.mddata.console.service.permission.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.console.entity.permission.ResourceButton;
import top.mddata.console.mapper.permission.ResourceButtonMapper;
import top.mddata.console.service.permission.ResourceButtonService;

/**
 * 按钮 服务层实现。
 *
 * @author henhen6
 * @since 2025-11-12 16:27:16
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceButtonServiceImpl extends SuperServiceImpl<ResourceButtonMapper, ResourceButton> implements ResourceButtonService {

}
