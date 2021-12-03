package org.wizard.marketing.core.dao;

import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.Condition;
import org.wizard.marketing.core.beans.CombCondition;
import org.wizard.marketing.core.utils.EventUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:12 下午
 * @Desc: ClickHouse查询器
 */
@Slf4j
public class ClickHouseQuerier {
    Connection conn;

    public ClickHouseQuerier(Connection conn) {
        this.conn = conn;
    }

    /**
     * 根据组合条件及查询的时间范围，得到返回结果的[1212]形式的字符串序列
     *
     * @param deviceId        账户ID
     * @param combCondition   行为组合条件
     * @param queryRangeStart 查询时间范围起始
     * @param queryRangeEnd   查询时间范围结束
     * @return 用户做过的组合条件中事件的字符串序列
     */
    public String getCombConditionStr(String deviceId, CombCondition combCondition, long queryRangeStart, long queryRangeEnd) throws Exception {
        String querySql = combCondition.getQuerySql();
        PreparedStatement stat = conn.prepareStatement(querySql);

        stat.setString(1, deviceId);
        stat.setLong(2, queryRangeStart);
        stat.setLong(3, queryRangeEnd);

        // 从组合条件中取出该组合所关心的事件列表
        List<Condition> conditionList = combCondition.getConditionList();
        List<String> ids = conditionList.stream().map(Condition::getEventId).collect(Collectors.toList());

        // 遍历ClickHouse返回的结果
        ResultSet resultSet = stat.executeQuery();
        StringBuilder sb = new StringBuilder();
        while (resultSet.next()) {
            String eventId = resultSet.getString(1);
            // 根据eventId到组合条件的事件列表中找到对应的索引号, 来作为最终结果拼凑
            sb.append(ids.indexOf(eventId) + 1);
        }

        return sb.toString();
    }

    /**
     * 根据组合条件及查询的时间范围，查询该组合出现的次数
     *
     * @param deviceId        账户ID
     * @param combCondition   行为组合条件
     * @param queryRangeStart 查询时间范围起始
     * @param queryRangeEnd   查询时间范围结束
     * @return 出现的次数
     */
    public int getCombConditionCount(String deviceId, CombCondition combCondition, long queryRangeStart, long queryRangeEnd) throws Exception {
        // TODO 缓存读处理

        // 先查询到用户在组合条件中做过的事件的字符串序列
        String eventSeqStr = getCombConditionStr(deviceId, combCondition, queryRangeStart, queryRangeEnd);
        // 调用工具，来获取事件序列与正则表达式的匹配次数--即组合条件发生的次数
        int count = EventUtils.eventSeqStrMatchRegexCount(eventSeqStr, combCondition.getMatchPattern());

        // TODO 缓存写处理

        log.debug("在ClickHouse中查询组合事件条件，得到的事件序列字符串: {}, 正则表达式: {}, 匹配结果: {}", eventSeqStr, combCondition.getMatchPattern(), count);
        return count;
    }

}
