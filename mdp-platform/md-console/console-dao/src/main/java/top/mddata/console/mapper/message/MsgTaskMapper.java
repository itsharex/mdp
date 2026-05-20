package top.mddata.console.mapper.message;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.mddata.base.mvcflex.mapper.SuperMapper;
import top.mddata.console.entity.message.MsgTask;

import java.util.List;

/**
 * 消息任务 映射层。
 *
 * @author henhen6
 * @since 2025-12-21 00:02:22
 */
@Repository
public interface MsgTaskMapper extends SuperMapper<MsgTask> {

    @Select("select * from mdc_msg_task where title = #{title}")
    List<MsgTask> listByTitle(@Param("title") String title);
}
