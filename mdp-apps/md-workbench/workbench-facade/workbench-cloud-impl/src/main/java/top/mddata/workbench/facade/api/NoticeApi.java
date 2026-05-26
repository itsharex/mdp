package top.mddata.workbench.facade.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import top.mddata.base.base.R;
import top.mddata.common.constant.AppConstants;
import top.mddata.workbench.entity.Notice;
import top.mddata.workbench.entity.NoticeRecipient;

import java.util.List;

/**
 * 跨服务-通知实现类
 * @author henhen6
 * @since 2025/12/26 10:21
 */
@FeignClient(name = AppConstants.WORKBENCH_SERVER, path = "/notice")
public interface NoticeApi {

    /**
     * 批量保存 站内通知接收人
     * @param recipientList 站内通知接收人
     */
    @PostMapping("/saveBatchNoticeRecipient")
    R<Boolean> saveBatchNoticeRecipient(List<NoticeRecipient> recipientList);

    /**
     * 保存 通知
     * @param notice 通知
     */
    @PostMapping("/save")
    R<Boolean> save(Notice notice);
}
