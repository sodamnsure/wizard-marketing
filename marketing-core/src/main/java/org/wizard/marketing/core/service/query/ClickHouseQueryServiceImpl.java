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
    public int queryCountCondition(String deviceId, ConditionBean condition) throws Exception {
        log.debug("收到一个clickhouse次数查询请求，参数为: deviceId = [{}], condition = [{}]", deviceId, condition.getQuerySql());
        return queryCountCondition(deviceId, condition, condition.getStartTime(), condition.getEndTime());
    }

    /**
     * 次数条件查询方法重载方法： 可指定时间范围
     */
    public int queryCountCondition(String deviceId, ConditionBean condition, Long startTime, Long endTime) throws Exception {
        PreparedStatement pst = ckConn.prepareStatement(condition.getQuerySql());
        pst.setString(1, deviceId);
        pst.setLong(2, startTime);
        pst.setLong(3, endTime);

        long result = 0;
        ResultSet resultSet = pst.executeQuery();
        while (resultSet.next()) {
            result = resultSet.getLong("cnt");
        }
        log.debug("次数查询结果为[{}]", result);

        return (int) result;
    }

    /**
     * 序列条件查询方法
     */
    public int querySequenceCondition(String deviceId, SequenceConditionBean sequenceConditionBean) throws SQLException {
        int size = sequenceConditionBean.getConditions().size();

        log.debug("收到一个clickhouse序列查询请求，参数为: deviceId = [{}], condition = [{}]", deviceId, sequenceConditionBean.getSequenceQuerySql());

        PreparedStatement pst = ckConn.prepareStatement(sequenceConditionBean.getSequenceQuerySql());
        pst.setString(1, deviceId);
        pst.setLong(2, sequenceConditionBean.getStartTime());
        pst.setLong(3, sequenceConditionBean.getEndTime());

        ResultSet resultSet = pst.executeQuery();
        int maxStep = 0;
        int offset = 1;
        while (resultSet.next()) {
            for (int i = 1; i <= size; i++) {
                int matchResult = resultSet.getInt(i);
                if (matchResult == 1) {
                    maxStep = size - (i - offset);
                    break;
                }
            }
        }

        return maxStep;
    }
}
