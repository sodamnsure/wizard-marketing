package org.wizard.marketing.core.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.client.Connection;
import org.wizard.marketing.core.beans.*;
import org.wizard.marketing.core.common.operators.CompareOperator;
import org.wizard.marketing.core.service.query.ClickHouseQueryServiceImpl;
import org.wizard.marketing.core.service.query.HbaseQueryServiceImpl;
import org.wizard.marketing.core.utils.ConnectionUtils;
import org.wizard.marketing.core.utils.RuleMonitor;

import java.util.List;
import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc:
 */
@Slf4j
public class MatchRuleFunction extends KeyedProcessFunction<String, EventBean, ResultBean> {
    Connection hbaseConn;
    HbaseQueryServiceImpl hbaseQueryService;
    ClickHouseQueryServiceImpl clickHouseQueryService;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 获取一个hbase的连接
        hbaseConn = ConnectionUtils.getHbaseConnection();
        // 获取一个clickhouse的jdbc连接
        java.sql.Connection ckConn = ConnectionUtils.getClickHouseConnection();


        // 构造一个hbase的查询服务
        hbaseQueryService = new HbaseQueryServiceImpl(hbaseConn);
        // 构造一个clickhouse的查询服务
        clickHouseQueryService = new ClickHouseQueryServiceImpl(ckConn);

    }

    @Override
    public void processElement(EventBean event, Context context, Collector<ResultBean> collector) throws Exception {
        /*
         * 获取规则
         */
        RuleBean rule = RuleMonitor.getRule();

        /*
         * 判断当前事件是否是规则定义的触发事件
         */
        if (!CompareOperator.compareUnit(rule.getTriggerEvent(), event)) return;
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
                return;
            }
        }

        /*
         * 计算次数条件是否满足
         */
        List<ConditionBean> countConditions = rule.getCountConditions();
        if (countConditions != null && countConditions.size() > 0) {
            log.debug("行为次数条件不为空，开始查询.......");
            for (ConditionBean condition : countConditions) {
                int count = clickHouseQueryService.queryCountCondition(event.getDeviceId(), condition);
                // 如果查询到一个行为次数条件不满足，则整个规则计算结束
                log.debug("次数条件阈值为: [{}], 查询到的结果为: [{}], 用户ID为: [{}]}", condition.getThreshold(), count, event.getDeviceId());
                if (count < condition.getThreshold()) return;
            }
        }

        /*
         * 计算序列条件是否满足
         */
        List<SequenceConditionBean> sequenceConditions = rule.getSequenceConditions();
        if (sequenceConditions != null && sequenceConditions.size() > 0) {
            log.debug("序列次数条件不为空，开始查询.......");
            for (SequenceConditionBean sequenceConditionBean : sequenceConditions) {
                int maxStep = clickHouseQueryService.querySequenceCondition(event.getDeviceId(), sequenceConditionBean);
                // 判断结果的最大完成步骤号，如果小于序列条件中的事件数，则不满足，整个规则计算结束
                if (maxStep < sequenceConditionBean.getConditions().size()) {
                    log.debug("序列次数条件的事件数为: [{}], 查询完成的最大步骤号为: [{}], 不满足条件", sequenceConditionBean.getConditions().size(), maxStep);
                    return;
                }
            }
        }

        log.info("规则 [{}] 完全匹配, 触发事件为: [{}], 匹配计算完成时间为: [{}]", rule.getRuleId(), event.getEventId(), System.currentTimeMillis());
        /*
         * 输出规则完全匹配的结果
         */
        ResultBean resultBean = new ResultBean();
        resultBean.setDeviceId(event.getDeviceId());
        resultBean.setRuleId(rule.getRuleId());
        resultBean.setProcessTime(System.currentTimeMillis());
        resultBean.setTriggerTime(event.getTimeStamp());

        collector.collect(resultBean);
    }
}
