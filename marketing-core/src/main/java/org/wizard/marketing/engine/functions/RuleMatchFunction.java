package org.wizard.marketing.engine.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.engine.beans.EventBean;
import org.wizard.marketing.engine.beans.MarketingRule;
import org.wizard.marketing.engine.beans.ResultBean;
import org.wizard.marketing.engine.utils.EventUtils;
import org.wizard.marketing.engine.utils.RuleMonitor;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc: 规则匹配函数
 */
@Slf4j
public class RuleMatchFunction extends KeyedProcessFunction<String, EventBean, ResultBean> {

    @Override
    public void open(Configuration parameters) throws Exception {

    }

    @Override
    public void processElement(EventBean event, Context context, Collector<ResultBean> collector) throws Exception {
        MarketingRule rule = RuleMonitor.getRule();
        if (!EventUtils.eventMatchCondition(event, rule.getTriggerEvent())) return;

        // 查询用户画像

        // 查询行为组合
    }
}
