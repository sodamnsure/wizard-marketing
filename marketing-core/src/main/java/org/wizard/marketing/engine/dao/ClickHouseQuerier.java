package org.wizard.marketing.engine.dao;

import org.wizard.marketing.engine.beans.CombCondition;
import org.wizard.marketing.engine.beans.Condition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:12 下午
 * @Desc: ClickHouse查询器
 */
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
        // 遍历ClickHouse返回的结果
        ResultSet resultSet = stat.executeQuery();
        StringBuilder sb = new StringBuilder();
        while (resultSet.next()) {
            String eventId = resultSet.getString(1);
            // 根据eventId到组合条件的事件列表中找到对应的索引号, 来作为最终结果拼凑
            sb.append(conditionList.indexOf(eventId) + 1);
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
        // 先查询到用户在组合条件中做过的事件的字符串序列
        String eventSeqStr = getCombConditionStr(deviceId, combCondition, queryRangeStart, queryRangeEnd);
        // 然后取出组合条件中的正则表达式
        String pattern = combCondition.getMatchPattern();
        // 匹配正则表达式，得到匹配的次数
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(eventSeqStr);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }

}
