package org.wizard.marketing.core.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.ResultBean;
import org.wizard.marketing.core.beans.RuleBean;
import org.wizard.marketing.core.router.SimpleQueryRouter;
import org.wizard.marketing.core.utils.RuleMonitor;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc:
 */
@Slf4j
public class MatchRuleFunction extends KeyedProcessFunction<String, EventBean, ResultBean> {

    SimpleQueryRouter simpleQueryRouter;

    @Override
    public void open(Configuration parameters) throws Exception {
        simpleQueryRouter = new SimpleQueryRouter();
    }

    @Override
    public void processElement(EventBean event, Context context, Collector<ResultBean> collector) throws Exception {
        /*
         * 获取规则
         */
        RuleBean rule = RuleMonitor.getRule();

        boolean isMatch = simpleQueryRouter.ruleMatch(rule, event);
        if (!isMatch) return;

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
