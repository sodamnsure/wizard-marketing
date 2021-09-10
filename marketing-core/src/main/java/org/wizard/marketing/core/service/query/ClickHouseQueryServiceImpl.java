package org.wizard.marketing.core.service.query;

import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.ConditionBean;
import org.wizard.marketing.core.beans.SequenceConditionBean;

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

    /**
     * 次数条件查询方法
     */
    public int queryCountCondition(String deviceId, ConditionBean condition) throws SQLException {
        log.debug("收到一个clickhouse查询请求，参数为: deviceId = [{}], condition = [{}]", deviceId, condition.getQuerySql());

        PreparedStatement pst = ckConn.prepareStatement(condition.getQuerySql());
        pst.setString(1, deviceId);

        long result = 0;
        ResultSet resultSet = pst.executeQuery();
        while (resultSet.next()) {
            result = resultSet.getLong("cnt");
        }

        return (int) result;

    }

    /**
     * 序列条件查询方法
     */
    public int querySequenceCondition(String deviceId, SequenceConditionBean sequenceConditionBean) throws SQLException {
        int size = sequenceConditionBean.getConditions().size();

        PreparedStatement pst = ckConn.prepareStatement(sequenceConditionBean.getSequenceQuerySql());
        pst.setString(1, deviceId);
        pst.setLong(2, sequenceConditionBean.getStartTime());
        pst.setLong(3, sequenceConditionBean.getEndTime());

        ResultSet resultSet = pst.executeQuery();
        resultSet.next();
        int maxStep = 0;
        int offset = 1;
        for (int i = 1; i <= size; i++) {
            int matchResult = resultSet.getInt(i);
            if (matchResult == 1) {
                maxStep = size - (i - offset);
                break;
            }
        }

        return maxStep;
    }
}
