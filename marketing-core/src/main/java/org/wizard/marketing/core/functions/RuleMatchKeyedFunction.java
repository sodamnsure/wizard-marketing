package org.wizard.marketing.core.functions;

import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.ResultBean;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc:
 */
public class RuleMatchKeyedFunction extends KeyedProcessFunction<String, EventBean, ResultBean> {

    @Override
    public void processElement(EventBean eventBean, Context context, Collector<ResultBean> collector) throws Exception {

    }
}
