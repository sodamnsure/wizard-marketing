package org.wizard.marketing.core.router;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ListState;
import org.apache.hadoop.hbase.client.Connection;
import org.wizard.marketing.core.beans.ConditionBean;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.RuleBean;
import org.wizard.marketing.core.beans.SequenceConditionBean;
import org.wizard.marketing.core.common.operators.CompareOperator;
import org.wizard.marketing.core.service.query.ClickHouseQueryServiceImpl;
import org.wizard.marketing.core.service.query.HbaseQueryServiceImpl;
import org.wizard.marketing.core.service.query.StateQueryServiceImpl;
import org.wizard.marketing.core.utils.ConnectionUtils;
import org.wizard.marketing.core.utils.SegmentQueryUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/23 7:41 下午
 * @Desc: 分段查询路由器
 */
@Slf4j
public class SegmentQueryRouter {
    HbaseQueryServiceImpl hbaseQueryService;
    ClickHouseQueryServiceImpl clickHouseQueryService;
    StateQueryServiceImpl stateQueryService;

    public SegmentQueryRouter(ListState<EventBean> eventListState) throws Exception {
        // 获取一个hbase的连接
        Connection hbaseConn = ConnectionUtils.getHbaseConnection();
        // 获取一个clickhouse的jdbc连接
        java.sql.Connection ckConn = ConnectionUtils.getClickHouseConnection();


        // 构造一个hbase的查询服务
        hbaseQueryService = new HbaseQueryServiceImpl(hbaseConn);
        // 构造一个clickhouse的查询服务
        clickHouseQueryService = new ClickHouseQueryServiceImpl(ckConn);
        // 构造一个state的查询服务
        stateQueryService = new StateQueryServiceImpl(eventListState);
    }

    /**
     * 规则匹配逻辑
     */
    public boolean ruleMatch(RuleBean rule, EventBean event, ListState<EventBean> eventListState) throws Exception {
        /*
         * 判断当前事件是否是规则定义的触发事件
         */
        if (!CompareOperator.compareUnit(rule.getTriggerEvent(), event)) return false;
        log.info("规则 [{}] 被触发, 触发事件为: [{}], 触发时间为: [{}]", rule.getRuleId(), event.getEventId(), System.currentTimeMillis());

        /*
         * 计算画像条件是否满足
         */
        Map<String, String> profileConditions = rule.getProfileConditions();
        if (profileConditions != null) {
            log.debug("画像属性条件不为空，开始查询.......");
            boolean profileQueryResult = hbaseQueryService.queryProfileCondition(event.getDeviceId(), profileConditions);
            // 如果画像属性条件查询结果为false,则整个规则计算结束
            if (!profileQueryResult) {
                log.debug("画像属性条件查询结果为false,该用户: [{}] 规则计算结束", event.getDeviceId());
                return false;
            }
        }

        // 获取当前时间对应的查询分界点
        Long segmentPoint = SegmentQueryUtils.getSegmentPoint(event.getTimeStamp());

        /*
         * 计算次数条件是否满足
         */
        List<ConditionBean> countConditions = rule.getCountConditions();
        if (countConditions != null && countConditions.size() > 0) {
            log.debug("行为次数条件不为空，开始查询.......");
            for (ConditionBean condition : countConditions) {
                // 判断条件中的时间跨度，落在分界点的左边? 右边? 跨界?
                if (condition.getEndTime() < segmentPoint) {
                    log.debug("次数查询::分界点左边查询 只查询ClickHouse");

                    int count = clickHouseQueryService.queryCountCondition(event.getDeviceId(), condition);
                    // 如果查询到一个行为次数条件不满足，则整个规则计算结束
                    log.debug("次数条件阈值为: [{}], 查询到的结果为: [{}], 用户ID为: [{}]}", condition.getThreshold(), count, event.getDeviceId());
                    if (count < condition.getThreshold()) return false;
                } else if (condition.getStartTime() >= segmentPoint) {
                    log.debug("次数查询::分界点右边查询 只查询State");

                    int count = stateQueryEventCount(eventListState, condition, condition.getStartTime(), condition.getEndTime());
                    if (count < condition.getThreshold()) return false;
                } else {
                    log.debug("次数查询::跨界查询::查询State");
                    // 先查状态
                    int countInState = stateQueryEventCount(eventListState, condition, segmentPoint, condition.getEndTime());
                    if (countInState < condition.getThreshold()) {
                        log.debug("次数查询::跨界查询::查询ClickHouse");

                        int countInClickHouse = clickHouseQueryService.queryCountCondition(event.getDeviceId(), condition, condition.getStartTime(), segmentPoint);
                        if (countInState + countInClickHouse < condition.getThreshold()) return false;
                    }
                }

            }
        }

        /*
         * 计算序列条件是否满足
         */
        List<SequenceConditionBean> sequenceConditions = rule.getSequenceConditions();
        if (sequenceConditions != null && sequenceConditions.size() > 0) {
            log.debug("序列次数条件不为空，开始查询.......");
            for (SequenceConditionBean sequenceConditionBean : sequenceConditions) {
                if (sequenceConditionBean.getEndTime() < segmentPoint) {
                    log.debug("序列查询::分界点左边查询 只查询ClickHouse");

                    int maxStep = clickHouseQueryService.querySequenceCondition(event.getDeviceId(), sequenceConditionBean);
                    // 判断结果的最大完成步骤号，如果小于序列条件中的事件数，则不满足，整个规则计算结束
                    if (maxStep < sequenceConditionBean.getConditions().size()) {
                        log.debug("序列次数条件的事件数为: [{}], 查询完成的最大步骤号为: [{}], 不满足条件", sequenceConditionBean.getConditions().size(), maxStep);
                        return false;
                    }

                } else if (sequenceConditionBean.getStartTime() >= segmentPoint) {
                    log.debug("序列查询::分界点右边查询 只查询State");

                    int step = stateQueryService.queryEventSequence(sequenceConditionBean.getConditions(), sequenceConditionBean.getStartTime(), sequenceConditionBean.getEndTime());
                    if (step < sequenceConditionBean.getConditions().size()) return false;
                } else {
                    log.debug("序列查询::跨界查询");

                    int step1 = clickHouseQueryService.querySequenceCondition(event.getDeviceId(), sequenceConditionBean, sequenceConditionBean.getStartTime(), segmentPoint);
                    List<ConditionBean> conditions = sequenceConditionBean.getConditions();
                    if (step1 < conditions.size()) {
                        // 根据ClickHouse中最大匹配数，来截取单个序列的条件组
                        List<ConditionBean> cutOutConditions = conditions.subList(step1, conditions.size());
                        int step2 = stateQueryService.queryEventSequence(cutOutConditions, segmentPoint, sequenceConditionBean.getEndTime());
                        if (step1 + step2 < conditions.size()) return false;
                    }
                }

            }
        }

        log.info("规则 [{}] 完全匹配, 触发事件为: [{}], 匹配计算完成时间为: [{}]", rule.getRuleId(), event.getEventId(), System.currentTimeMillis());

        return true;
    }

    /**
     * 工具方法: 在state中计算指定事件的发生次数
     */
    private int stateQueryEventCount(ListState<EventBean> eventListState, ConditionBean condition, Long startTime, Long endTime) throws Exception {
        int count = 0;
        Iterable<EventBean> eventIterable = eventListState.get();
        for (EventBean eventInState : eventIterable) {
            // 判断事件是否落在规则条件的时间区间
            if (eventInState.getTimeStamp() >= startTime && eventInState.getTimeStamp() <= endTime) {
                // 判断State中最近的事件与次数条件中是否匹配
                if (CompareOperator.compareUnit(condition, eventInState)) {
                    count++;
                }
            }
        }
        return count;
    }
}
