package org.wizard.marketing.core.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.client.Connection;
import org.wizard.marketing.core.beans.ConditionBean;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.ResultBean;
import org.wizard.marketing.core.beans.RuleBean;
import org.wizard.marketing.core.common.operators.CompareOperator;
import org.wizard.marketing.core.service.query.ClickQueryServiceImpl;
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
    ClickQueryServiceImpl CKQueryService;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 获取一个hbase的连接
        hbaseConn = ConnectionUtils.getHbaseConnection();
        // 获取一个clickhouse的jdbc连接
        java.sql.Connection ckConn = ConnectionUtils.getClickHouseConnection();


        // 构造一个hbase的查询服务
        hbaseQueryService = new HbaseQueryServiceImpl(hbaseConn);
        // 构造一个clickhouse的查询服务
        CKQueryService = new ClickQueryServiceImpl(ckConn);

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
        log.debug("判断当前事件是否是规则定义的触发事件......");
        if (!CompareOperator.compareUnit(rule.getTriggerEvent(), event)) return;
        log.debug("规则被触发...........");

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
                int count = CKQueryService.queryActionCountCondition(event.getDeviceId(), condition);
                // 如果查询到一个行为次数条件不满足，则整个规则计算结束
                log.debug("规则条件为: [{}], 查询到的结果为: [{}]", condition.getThreshold(), count);
                if (count < condition.getThreshold()) return;
            }
        }

        /*
         * 计算序列条件是否满足
         */
    }
}
