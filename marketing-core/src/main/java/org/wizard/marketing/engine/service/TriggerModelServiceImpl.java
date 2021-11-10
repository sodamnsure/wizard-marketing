package org.wizard.marketing.engine.service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.common.state.ListState;
import org.wizard.marketing.engine.beans.CombCondition;
import org.wizard.marketing.engine.beans.EventBean;
import org.wizard.marketing.engine.dao.ClickHouseQuerier;
import org.wizard.marketing.engine.dao.HbaseQuerier;
import org.wizard.marketing.engine.dao.StateQuerier;
import org.wizard.marketing.engine.utils.ConfigNames;
import org.wizard.marketing.engine.utils.ConnectionUtils;
import org.wizard.marketing.engine.utils.CrossTimeQueryUtils;
import org.wizard.marketing.engine.utils.EventUtils;

import java.sql.Connection;
import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:09 下午
 * @Desc: 触发性规则模型查询服务
 */
public class TriggerModelServiceImpl {
    ClickHouseQuerier clickHouseQuerier;
    HbaseQuerier hbaseQuerier;
    StateQuerier stateQuerier;

    /**
     * 构造函数
     *
     * @param listState 状态List
     * @throws Exception 异常
     */
    public TriggerModelServiceImpl(ListState<EventBean> listState) throws Exception {
        Config config = ConfigFactory.load();

        Connection clickHouseConn = ConnectionUtils.getClickHouseConnection();
        clickHouseQuerier = new ClickHouseQuerier(clickHouseConn);

        org.apache.hadoop.hbase.client.Connection hbaseConn = ConnectionUtils.getHbaseConnection();
        String profileTable = config.getString(ConfigNames.HBASE_PROFILE_TABLE);
        String profileFamily = config.getString(ConfigNames.HBASE_PROFILE_FAMILY);
        hbaseQuerier = new HbaseQuerier(hbaseConn, profileTable, profileFamily);

        stateQuerier = new StateQuerier(listState);

    }

    /**
     * 画像条件匹配
     *
     * @param profileCondition 画像条件
     * @param deviceId         账户ID
     * @return 是否匹配
     * @throws Exception 异常
     */
    public boolean matchProfileCondition(Map<String, String> profileCondition, String deviceId) throws Exception {
        return hbaseQuerier.queryProfileConditionIsMatch(profileCondition, deviceId);
    }

    /**
     * 计算单个行为组合条件是否匹配
     *
     * @param event         单个事件
     * @param combCondition 组合条件
     * @return 是否匹配
     * @throws Exception 异常
     */
    public boolean matchCombCondition(EventBean event, CombCondition combCondition) throws Exception {
        // 获取当前事件时间对应的分界点
        long segmentPoint = CrossTimeQueryUtils.getSegmentPoint(event.getTimeStamp());
        // 判断条件的时间区间是否跨分界点
        long timeRangeStart = combCondition.getTimeRangeStart();
        long timeRangeEnd = combCondition.getTimeRangeEnd();
        if (timeRangeStart >= segmentPoint) {
            // 查状态
            int count = stateQuerier.getCombConditionCount(event.getDeviceId(), combCondition, timeRangeStart, timeRangeEnd);
            return count >= combCondition.getMinLimit() && count <= combCondition.getMaxLimit();
        } else if (timeRangeEnd < segmentPoint) {
            // 查ClickHouse
            int count = clickHouseQuerier.getCombConditionCount(event.getDeviceId(), combCondition, timeRangeStart, timeRangeEnd);
            return count >= combCondition.getMinLimit() && count <= combCondition.getMaxLimit();
        } else {
            // 先查一次state，看是否能提前结束
            int stateCount = stateQuerier.getCombConditionCount(event.getDeviceId(), combCondition, segmentPoint, timeRangeEnd);
            if (stateCount >= combCondition.getMinLimit()) return true;

            // 先从ClickHouse中查询满足条件的事件序列字符串，拼接state中查询到满足条件的事件序列字符串，作为整体匹配正则表达式
            String str1 = clickHouseQuerier.getCombConditionStr(event.getDeviceId(), combCondition, timeRangeStart, segmentPoint);
            String str2 = stateQuerier.getCombConditionStr(event.getDeviceId(), combCondition, segmentPoint, timeRangeEnd);
            int count = EventUtils.eventSeqStrMatchRegexCount(str1 + str2, combCondition.getMatchPattern());

            // 判断是否匹配成功
            return count >= combCondition.getMinLimit() && count <= combCondition.getMaxLimit();
        }
    }

}
