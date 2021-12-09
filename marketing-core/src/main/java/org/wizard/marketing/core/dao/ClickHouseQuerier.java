package org.wizard.marketing.core.dao;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.BufferData;
import org.wizard.marketing.core.beans.CombCondition;
import org.wizard.marketing.core.beans.Condition;
import org.wizard.marketing.core.buffer.BufferManager;
import org.wizard.marketing.core.buffer.BufferManagerImpl;
import org.wizard.marketing.core.constants.InitialConfigConstants;
import org.wizard.marketing.core.utils.EventUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:12 下午
 * @Desc: ClickHouse查询器
 */
@Slf4j
public class ClickHouseQuerier {
    Connection conn;
    BufferManager bufferManager;
    long bufferTtl;

    public ClickHouseQuerier(Connection conn) {
        this.conn = conn;
        bufferManager = new BufferManagerImpl();

        Config config = ConfigFactory.load();
        bufferTtl = config.getLong(InitialConfigConstants.REDIS_BUFFER_TTL);
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
     * <p>该逻辑实现中，没有考虑一个问题
     * <p>valueMap中，可能同时存在多个时间区间，可能遇到一个满足条件的就返回了
     * <p>最好的实现是，先遍历一遍valueMap，从中找到最优的缓存区间数据，然后再去判断
     *
     * @param deviceId        账户ID
     * @param combCondition   行为组合条件
     * @param queryRangeStart 查询时间范围起始
     * @param queryRangeEnd   查询时间范围结束
     * @return 出现的次数
     */
    public int getCombConditionCount(String deviceId, CombCondition combCondition, long queryRangeStart, long queryRangeEnd) throws Exception {
        // 缓存读处理
        /*
            缓存在什么情况下有用
                缓存数据的时间范围为: [t3 ~ t8]
                查询条件的时间范围如下几种情况有效
                    1. [t3 ~ t8]  : 时间完全一致，直接用缓存的结果
                    2. [t3 ~ t10] : 如果缓存的数据的阈值 >= 条件的阈值，则直接返回缓存结果, 否则，用缓存的结果拼接 [t8 ~ t10]的查询结果，作为整个返回结果
                    3. [t1 ~ t8]  : 如果缓存的数据的阈值 >= 条件的阈值，则直接返回缓存结果, 否则，用[t1 ~ t3]的查询结果拼接缓存的结果，作为整个返回结果
                    4. [t1 ~ t10] : 如果缓存的数据的阈值 >= 条件的阈值，则直接返回缓存结果, 否则，无用

            可存在的优化空间：
                查询时间范围为[t1 ~ t10], 缓存中存在的时间范围可能既有[t1 ~ t8], 还有[t2 ~ t10] 等等，这样可以选择一个最优的情况，而不是全部遍历
         */

        String bufferKey = deviceId + ":" + combCondition.getCacheId();
        BufferData bufferData = bufferManager.getDataFromBuffer(bufferKey);
        Map<String, String> valueMap = bufferData.getValueMap();
        Set<String> keySet = valueMap.keySet();

        long current = System.currentTimeMillis();
        for (String key : keySet) {
            String[] split = key.split(":");
            long bufferStartTime = Long.parseLong(split[0]);
            long bufferEndTime = Long.parseLong(split[1]);

            // 判断缓存是否过期，做清除操作
            long bufferInsertTime = Long.parseLong(split[2]);
            if (System.currentTimeMillis() - bufferInsertTime >= bufferTtl) {
                bufferManager.deleteBufferKey(bufferKey, key);
            }

            String bufferSeqStr = valueMap.get(key);

            // 查询范围和缓存范围完全相同，直接返回缓存结果
            if (bufferStartTime == queryRangeStart && bufferEndTime == queryRangeEnd) {
                // 将原缓存结果删除
                bufferManager.deleteBufferKey(bufferKey, key);

                HashMap<String, String> putMap = new HashMap<>();
                putMap.put(bufferStartTime + ":" + bufferEndTime + ":" + current, bufferSeqStr);
                bufferManager.putDataToBuffer(bufferKey, putMap);

                return EventUtils.eventSeqStrMatchRegexCount(bufferSeqStr, combCondition.getMatchPattern());
            }

            // 左端点对齐，但是条件的时间范围包含缓存的时间范围
            if (bufferStartTime == queryRangeStart && bufferEndTime < queryRangeEnd) {
                int bufferCount = EventUtils.eventSeqStrMatchRegexCount(bufferSeqStr, combCondition.getMatchPattern());
                int queryMinCount = combCondition.getMinLimit();
                if (bufferCount >= queryMinCount) {
                    return bufferCount;
                } else {
                    // 调整查询时间，去ClickHouse中查询一小段
                    String rightSeqStr = getCombConditionStr(deviceId, combCondition, bufferEndTime, queryRangeEnd);
                    // 将原缓存结果删除
                    bufferManager.deleteBufferKey(bufferKey, key);

                    // 将结果写入缓存，更新插入时间
                    HashMap<String, String> putMap = new HashMap<>();
                    // 放入之前的缓存数据，三种类型：原buffer区间，右分段区间，原buffer区间+右分段区间
                    putMap.put(bufferStartTime + ":" + bufferEndTime + ":" + current, bufferSeqStr);
                    putMap.put(bufferEndTime + ":" + queryRangeEnd + ":" + current, rightSeqStr);
                    putMap.put(bufferStartTime + ":" + queryRangeEnd + ":" + current, bufferSeqStr + rightSeqStr);
                    bufferManager.putDataToBuffer(bufferKey, putMap);

                    return EventUtils.eventSeqStrMatchRegexCount(bufferSeqStr + rightSeqStr, combCondition.getMatchPattern());
                }
            }

            // 右端点对齐，但是条件的时间范围包含缓存的时间范围
            if (bufferStartTime > queryRangeStart && bufferEndTime == queryRangeEnd) {
                int bufferCount = EventUtils.eventSeqStrMatchRegexCount(bufferSeqStr, combCondition.getMatchPattern());
                int queryMinCount = combCondition.getMinLimit();
                if (bufferCount >= queryMinCount) {
                    return bufferCount;
                } else {
                    // 调整查询时间，去ClickHouse中查询一小段
                    String leftSeqStr = getCombConditionStr(deviceId, combCondition, queryRangeStart, bufferStartTime);

                    // 将原缓存结果删除
                    bufferManager.deleteBufferKey(bufferKey, key);

                    // 将结果写入缓存，更新插入时间
                    HashMap<String, String> putMap = new HashMap<>();
                    // 放入之前的缓存数据，三种类型：原buffer区间，右分段区间，原buffer区间+右分段区间
                    putMap.put(bufferStartTime + ":" + bufferEndTime + ":" + current, bufferSeqStr);
                    putMap.put(queryRangeStart + ":" + bufferStartTime + ":" + current, leftSeqStr);
                    putMap.put(queryRangeStart + ":" + queryRangeEnd + ":" + current, leftSeqStr + bufferSeqStr);
                    bufferManager.putDataToBuffer(bufferKey, putMap);

                    return EventUtils.eventSeqStrMatchRegexCount(leftSeqStr + bufferSeqStr, combCondition.getMatchPattern());
                }
            }

            // 条件的时间范围包含缓存的时间范围
            if (bufferStartTime > queryRangeStart && bufferEndTime < queryRangeEnd) {
                int bufferCount = EventUtils.eventSeqStrMatchRegexCount(bufferSeqStr, combCondition.getMatchPattern());
                int queryMinCount = combCondition.getMinLimit();
                if (bufferCount >= queryMinCount) {
                    // 将原缓存结果删除
                    bufferManager.deleteBufferKey(bufferKey, key);

                    // 将结果写入缓存，更新插入时间
                    HashMap<String, String> putMap = new HashMap<>();
                    // 放入之前的缓存数据，三种类型：原buffer区间，右分段区间，原buffer区间+右分段区间
                    putMap.put(bufferStartTime + ":" + bufferEndTime + ":" + current, bufferSeqStr);
                    bufferManager.putDataToBuffer(bufferKey, putMap);

                    return bufferCount;
                }
            }
        }

        // 先查询到用户在组合条件中做过的事件的字符串序列
        String eventSeqStr = getCombConditionStr(deviceId, combCondition, queryRangeStart, queryRangeEnd);
        // 将查询结果写入缓存
        HashMap<String, String> putMap = new HashMap<>();
        putMap.put(queryRangeStart + ":" + queryRangeEnd + ":" + current, eventSeqStr);
        bufferManager.putDataToBuffer(bufferKey, putMap);

        // 调用工具，来获取事件序列与正则表达式的匹配次数--即组合条件发生的次数
        int count = EventUtils.eventSeqStrMatchRegexCount(eventSeqStr, combCondition.getMatchPattern());

        log.debug("在ClickHouse中查询组合事件条件，得到的事件序列字符串: {}, 正则表达式: {}, 匹配结果: {}", eventSeqStr, combCondition.getMatchPattern(), count);
        return count;
    }

}
