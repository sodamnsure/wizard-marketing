package org.wizard.marketing.core.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.client.Connection;
import org.wizard.marketing.core.beans.Event;
import org.wizard.marketing.core.beans.Result;
import org.wizard.marketing.core.beans.Rule;
import org.wizard.marketing.core.common.operators.CompareOperator;
import org.wizard.marketing.core.service.query.HbaseQueryServiceImpl;
import org.wizard.marketing.core.utils.ConnectionUtils;
import org.wizard.marketing.core.utils.RuleMonitor;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc:
 */
@Slf4j
public class RuleMatchKeyedFunction extends KeyedProcessFunction<String, Event, Result> {
    Connection hbaseConn;
    HbaseQueryServiceImpl hbaseQueryService;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 获取一个hbase的连接
        hbaseConn = ConnectionUtils.getHbaseConnection();
        // 获取画像查询服务
        hbaseQueryService = new HbaseQueryServiceImpl(hbaseConn);
    }

    @Override
    public void processElement(Event event, Context context, Collector<Result> collector) throws Exception {
        /*
         * 获取规则
         */
        Rule rule = RuleMonitor.getRule();

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
            boolean profileQueryResult = hbaseQueryService.queryProfileCondition(event.getDeviceId(), profileConditions);
            // 如果画像属性条件查询结果为false,则整个规则计算结束
            if (!profileQueryResult) {
                log.debug("画像属性条件查询结果为false,该用户: [" + event.getDeviceId() + "] 规则计算结束");
                return;
            }
        }

        /*
         * 计算行为次数条件是否满足
         */

        /*
         * 计算行为次序条件是否满足
         */
    }
}
