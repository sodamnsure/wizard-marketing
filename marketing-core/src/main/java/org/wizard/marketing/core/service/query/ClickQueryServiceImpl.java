package org.wizard.marketing.core.service.query;

import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.ConditionBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/3 4:46 下午
 * @Desc:
 */
@Slf4j
public class ClickQueryServiceImpl implements QueryService {

    Connection ckConn;

    public ClickQueryServiceImpl(Connection conn) {
        this.ckConn = conn;
    }

    public int queryActionCountCondition(String deviceId, ConditionBean condition) throws SQLException {
        log.debug("收到一个clickhouse查询请求，参数为: deviceId = [{}], condition = [{}]", deviceId, condition);

        PreparedStatement pst = ckConn.prepareStatement(condition.getQuerySql());
        pst.setString(1, deviceId);

        long result = 0;
        ResultSet resultSet = pst.executeQuery();
        while (resultSet.next()) {
            result = resultSet.getLong("cnt");
        }

        return (int)result;

    }
}
