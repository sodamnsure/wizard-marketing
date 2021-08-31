package org.wizard.marketing.core.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.wizard.marketing.core.beans.Event;
import org.wizard.marketing.core.beans.Result;
import org.wizard.marketing.core.beans.Rule;
import org.wizard.marketing.core.common.operators.CompareOperator;
import org.wizard.marketing.core.utils.ConnectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc:
 */
@Slf4j
public class RuleMatchKeyedFunction extends KeyedProcessFunction<String, Event, Result> {
    Connection hbaseConn;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 获取一个hbase的连接
        hbaseConn = ConnectionUtils.getHbaseConnection();


    }

    @Override
    public void processElement(Event event, Context context, Collector<Result> collector) throws Exception {
        // 获取规则
        Rule rule = new Rule();

        /*
         * 判断当前事件是否是规则定义的触发事件
         */
        if (!CompareOperator.compareUnit(rule.getTriggerEvent(), event)) return;
        log.debug("规则被触发...........");

        /*
         * 计算画像条件是否满足
         */
        Map<String, String> userProfileConditions = rule.getUserProfileConditions();
        if (userProfileConditions != null) {
            Set<String> tags = userProfileConditions.keySet();

            Table table = hbaseConn.getTable(TableName.valueOf("user_profile"));
            Get get = new Get(event.getDeviceId().getBytes());
            for (String tag : tags) {
                get.addColumn("f".getBytes(), tag.getBytes());
            }

            org.apache.hadoop.hbase.client.Result result = table.get(get);

            for (String tag : tags) {
                byte[] value = result.getValue("f".getBytes(), tag.getBytes());
                System.out.println("查询到一个标签: " + tag + " = " + new String(value));
                if (!userProfileConditions.get(tag).equals(new String(value))) return;
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
