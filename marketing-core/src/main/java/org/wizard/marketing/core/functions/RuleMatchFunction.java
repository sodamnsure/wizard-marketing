package org.wizard.marketing.core.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.MarketingRule;
import org.wizard.marketing.core.beans.ResultBean;
import org.wizard.marketing.core.beans.TimerCondition;
import org.wizard.marketing.core.controller.TriggerModelController;
import org.wizard.marketing.core.utils.RuleMonitor;
import org.wizard.marketing.core.utils.StateDescContainer;

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
    ListState<Tuple2<MarketingRule, Long>> ruleTimerState;
    TriggerModelController triggerModelController;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 用模拟器获取一个规则
        MarketingRule rule = RuleMonitor.getRule();
        ruleList = Collections.singletonList(rule);
        // 获取触发形规则模型Controller
        listState = getRuntimeContext().getListState(StateDescContainer.getEventBeansDesc());
        triggerModelController = new TriggerModelController(listState);
        // 记录规则定时注册信息的State
        ruleTimerState = getRuntimeContext().getListState(StateDescContainer.getRuleTimerStateDesc());
    }

    @Override
    public void processElement(EventBean event, Context context, Collector<ResultBean> collector) throws Exception {
        // 将数据流事件放入state
        listState.add(event);
        log.debug("接收到事件, 用户ID为：{}, 用户事件为: {}", event.getDeviceId(), event.getEventId());

        // 遍历所有规则
        for (MarketingRule rule : ruleList) {
            // 规则匹配计算
            log.debug("匹配一个规则：{}", rule.getRuleId());
            boolean isMatch = triggerModelController.ruleIsMatch(rule, event);

            // 如果满足
            if (isMatch) {
                // 再判断这个规则是否是一个带定时条件的规则
                if (rule.isOnTimer()) {
                    // 注册定时器
                    List<TimerCondition> timerConditions = rule.getTimerConditions();
                    // 目前限定一个规则只有一个时间条件
                    TimerCondition timerCondition = timerConditions.get(0);
                    context.timerService().registerEventTimeTimer(event.getTimeStamp() + timerCondition.getTimeLate());
                    // 在定时信息State中记录
                    ruleTimerState.add(Tuple2.of(rule, event.getTimeStamp() + timerCondition.getTimeLate()));
                } else {
                    ResultBean resultBean = new ResultBean(event.getDeviceId(), rule.getRuleId(), event.getTimeStamp(), System.currentTimeMillis());
                    collector.collect(resultBean);
                }
            }
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, EventBean, ResultBean>.OnTimerContext ctx, Collector<ResultBean> out) throws Exception {
        Iterable<Tuple2<MarketingRule, Long>> ruleTimerStateIterable = ruleTimerState.get();
        for (Tuple2<MarketingRule, Long> tp : ruleTimerStateIterable) {
            // 判断"规则+定时点"，是否对应本次触发点
            if (tp.f1 == timestamp) {
                // 如果对应，检查该规则的定时条件
                MarketingRule rule = tp.f0;
                TimerCondition timerCondition = rule.getTimerConditions().get(0);
                // 调用service去检查在条件指定的时间范围内，组合事件发生的次数是否满足
                boolean b = triggerModelController.isMatchTimerCondition(ctx.getCurrentKey(), timerCondition,
                        timestamp - timerCondition.getTimeLate(), timestamp);
                if (b) {
                    ResultBean resultBean = new ResultBean(ctx.getCurrentKey(), rule.getRuleId(), timestamp, System.currentTimeMillis());
                    out.collect(resultBean);
                }
            }
        }
    }
}
