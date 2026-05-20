package top.mddata.console.service.permission.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mddata.base.mvcflex.service.impl.SuperServiceImpl;
import top.mddata.console.entity.permission.ResourceField;
import top.mddata.console.mapper.permission.ResourceFieldMapper;
import top.mddata.console.service.permission.ResourceFieldService;

/**
 * 字段权限 服务层实现。
 *
 * @author henhen6
 * @since 2025-11-12 16:27:16
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceFieldServiceImpl extends SuperServiceImpl<ResourceFieldMapper, ResourceField> implements ResourceFieldService {

}
