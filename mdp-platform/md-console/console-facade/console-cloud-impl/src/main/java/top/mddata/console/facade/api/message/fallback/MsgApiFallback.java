package top.mddata.console.facade.api.message.fallback;

import org.springframework.stereotype.Component;
import top.mddata.base.base.R;
import top.mddata.console.facade.api.message.MsgApi;
import top.mddata.console.message.dto.MsgSendDto;

/**
 *
 * @author henhen
 * @since 2026/5/10 23:08
 */
@Component
public class MsgApiFallback implements MsgApi {
    @Override
    public R<Long> sendByTemplateKey(MsgSendDto data) {
        return R.timeout();
    }
}
