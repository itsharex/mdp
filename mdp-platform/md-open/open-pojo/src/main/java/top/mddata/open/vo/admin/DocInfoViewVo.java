package top.mddata.open.vo.admin;

import lombok.Data;

/**
 * @author henhen6
 */
@Data
public class DocInfoViewVo {

    /**
     * 文档详情
     */
    private TornaDocInfoViewVo docInfoView;

    /**
     * 文档配置
     */
    private DocSettingVo docInfoConfig;

}
