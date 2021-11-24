package org.wizard.marketing.engine.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.engine.beans.EventBean;
import org.wizard.marketing.engine.beans.MarketingRule;
import org.wizard.marketing.engine.beans.ResultBean;
import org.wizard.marketing.engine.controller.TriggerModelController;
import org.wizard.marketing.engine.utils.RuleMonitor;
import org.wizard.marketing.engine.utils.StateDescContainer;

import java.util.Collections;
import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc: 规则匹配函数
 */
@Slf4j
public class RuleMatchFunction extends KeyedProcessFunction<String, EventBean, ResultBean> {
    List<MarketingRule> ruleList;
    ListState<EventBean> listState;
    TriggerModelController triggerModelController;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 用模拟器获取一个规则
        MarketingRule rule = RuleMonitor.getRule();
        ruleList = Collections.singletonList(rule);
        // 获取触发形规则模型Controller
        listState = getRuntimeContext().getListState(StateDescContainer.getEventBeansDesc());
        triggerModelController = new TriggerModelController(listState);
    }

    @Override
    public void processElement(EventBean event, Context context, Collector<ResultBean> collector) throws Exception {
        // 将数据流事件放入state
        listState.add(event);
        log.debug("接收到事件：{}", event);
        for (MarketingRule rule : ruleList) {
            log.debug("匹配一个规则：{}", rule.getRuleId());
            boolean isMatch = triggerModelController.ruleIsMatch(rule, event);
            log.debug("规则{}，计算完毕，结果为 {}", rule.getRuleId(), isMatch);
            if (isMatch) {
                ResultBean resultBean = new ResultBean(event.getDeviceId(), rule.getRuleId(), event.getTimeStamp(), System.currentTimeMillis());
                collector.collect(resultBean);
            }
        }
    }
}
