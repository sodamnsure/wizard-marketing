package org.wizard.marketing.core.service.query;

import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.Condition;
import org.wizard.marketing.core.beans.Event;

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
public class ClickHouseQueryServiceImpl implements QueryService {

    Connection ckConn;

    public ClickHouseQueryServiceImpl(Connection conn) {
        this.ckConn = conn;
    }

    public int queryActionCountCondition(String deviceId, Condition condition) throws SQLException {
        log.debug("收到一个clickhouse查询请求，参数为: deviceId = [{}], condition = [{}]", deviceId, condition);
        String sql = "" +
                "select " +
                "count(*) as cnt\n" +
                "from default.event_detail\n" +
                "where eventId = 'S'\n" +
                "  and properties['p1'] = 'v6'\n" +
                "  and properties['p7'] = 'v1'\n" +
                "  and deviceId = ?\n" +
                "  and timeStamp between ? and ?";
        PreparedStatement pst = ckConn.prepareStatement(sql);
        pst.setString(1, deviceId);
        pst.setLong(2, condition.getStartTime());
        pst.setLong(3, condition.getEndTime());

        long result = 0;
        ResultSet resultSet = pst.executeQuery();
        while (resultSet.next()) {
            result = resultSet.getLong("cnt");
        }

        return 1;

    }
}
