package top.mddata.console.system.service;

import top.mddata.base.mvcflex.service.SuperService;
import top.mddata.console.entity.system.Dict;
import top.mddata.console.vo.system.DictVo;

import java.util.List;

/**
 * 字典 服务层。
 *
 * @author henhen6
 * @since 2025-11-12 16:21:25
 */
public interface DictService extends SuperService<Dict> {
    /**
     * 检测 字典标识 是否存在
     * @param uniqKey 字典标识
     * @param id 需要排除的字典id
     * @return 存在=true
     */
    Boolean checkByUniqKey(String uniqKey, Long id);

    /**
     * 通过枚举导入字典
     * @param list 枚举数据
     * @return 是否成功
     */
    Boolean importDictByEnum(List<DictVo> list);
}
