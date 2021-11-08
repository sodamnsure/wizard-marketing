package org.wizard.marketing.engine.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.engine.beans.EventBean;
import org.wizard.marketing.engine.beans.ResultBean;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc:
 */
@Slf4j
public class MatchRuleFunction extends KeyedProcessFunction<String, EventBean, ResultBean> {



    @Override
    public void open(Configuration parameters) throws Exception {

    }

    @Override
    public void processElement(EventBean event, Context context, Collector<ResultBean> collector) throws Exception {

    }
}
