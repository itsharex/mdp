package top.mddata.base.mybatisflex.dialect;

import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;

import java.util.List;

import static com.mybatisflex.core.constant.SqlConnector.AND;
import static com.mybatisflex.core.constant.SqlOperator.EQUALS;

/**
 * 权限处理
 */
public class AuthDialectImpl extends CommonsDialectImpl {

    private static final String NAME = "mdc_msg_task";
    private static final String ID = "created_by";

    /**
     * 分页查询
     *
     * @param queryWrapper queryWrapper
     * @param operateType  操作类型
     */
    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return;
        }
        for (QueryTable queryTable : queryTables) {
            if (NAME.equals(queryTable.getName())) {
                queryWrapper.and(QueryColumnBehavior.castCondition(QueryCondition.create(new QueryColumn("", ID), EQUALS, 1)));
            }
        }
        super.prepareAuth(queryWrapper, operateType);
    }

    @Override
    public void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
        if (NAME.equals(tableName)) {
            sql.append(AND).append(wrap(ID)).append(EQUALS).append(1);
        }
        super.prepareAuth(schema, tableName, sql, operateType);
    }

    /**
     * 单体查询
     *
     * @param tableInfo   tableInfo
     * @param sql         sql
     * @param operateType 操作类型
     */
    @Override
    public void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
        if (NAME.equals(tableInfo.getTableName())) {
            sql.append(AND).append(wrap(ID)).append(EQUALS).append(1);
        }
        super.prepareAuth(tableInfo, sql, operateType);
    }
}
