package org.wizard.marketing.core.functions;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.ResultBean;
import org.wizard.marketing.core.beans.RuleBean;
import org.wizard.marketing.core.utils.ConnectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc:
 */
public class RuleMatchKeyedFunction extends KeyedProcessFunction<String, EventBean, ResultBean> {
    Connection hbaseConn;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 获取一个hbase的连接
        hbaseConn = ConnectionUtils.getHbaseConnection();


    }

    @Override
    public void processElement(EventBean event, Context context, Collector<ResultBean> collector) throws Exception {
        // 获取规则
        RuleBean rule = new RuleBean();

        /*
          判断当前事件是否是规则定义的触发事件
         */

        /*
          计算画像条件是否满足
         */
        Map<String, String> userProfileConditions = rule.getUserProfileConditions();
        if (userProfileConditions != null) {
            Set<String> tags = userProfileConditions.keySet();

            Table table = hbaseConn.getTable(TableName.valueOf("user_profile"));
            Get get = new Get(event.getDeviceId().getBytes());
            for (String tag : tags) {
                get.addColumn("f".getBytes(), tag.getBytes());
            }

            Result result = table.get(get);

            for (String tag : tags) {
                byte[] value = result.getValue("f".getBytes(), tag.getBytes());
                System.out.println("查询到一个标签: " + tag + " = " + new String(value));
                if (!userProfileConditions.get(tag).equals(new String(value))) return;
            }

        }

        // 计算行为次数条件是否满足

        // 计算行为次序条件是否满足
    }
}
