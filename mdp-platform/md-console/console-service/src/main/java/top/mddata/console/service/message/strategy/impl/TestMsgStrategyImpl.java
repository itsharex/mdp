package top.mddata.console.service.message.strategy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mddata.console.service.message.strategy.MsgTaskStrategy;
import top.mddata.console.service.message.strategy.dto.MsgResult;
import top.mddata.console.service.message.strategy.dto.MsgTaskParam;

/**
 * 示例实现类
 * @author henhen
 */
public class TestMsgStrategyImpl implements MsgTaskStrategy {
    private static final Logger log = LoggerFactory.getLogger(TestMsgStrategyImpl.class);

    @Override
    public MsgResult exec(MsgTaskParam msgParam) {

        log.info("a {}", msgParam);

        return MsgResult.builder().result("保存成功").build();
    }
}
