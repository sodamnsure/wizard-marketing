package org.wizard.marketing.core.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.core.beans.*;
import org.wizard.marketing.core.controller.TriggerModelController;
import org.wizard.marketing.core.utils.RuleMonitor;
import org.wizard.marketing.core.utils.StateDescContainer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:14 下午
 * @Desc: 规则匹配函数
 */
@Slf4j
public class RuleMatchFunction extends KeyedProcessFunction<String, DynamicKeyedBean, ResultBean> {
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
    public void processElement(DynamicKeyedBean dynamicKeyedBean, Context context, Collector<ResultBean> collector) throws Exception {
        // 将数据流事件放入state
        EventBean event = dynamicKeyedBean.getEventBean();
        listState.add(event);

        // 遍历所有规则
        for (MarketingRule rule : ruleList) {
            // 规则匹配计算
            log.debug("匹配一个规则：{}", rule.getRuleId());
            // 判断规则中的分组依据，和当前进入的数据的分组依据，是否相同，相同才计算
            boolean isMatch = false;
            if (rule.getKeyByFields().equals(dynamicKeyedBean.getKeyNames())) {
                isMatch = triggerModelController.ruleIsMatch(rule, event);
            }

            // 如果满足
            if (isMatch) {
                // 再判断这个规则是否是一个带定时条件的规则
                if (rule.isOnTimer()) {
                    // 注册定时器
                    List<TimerCondition> timerConditions = rule.getTimerConditions();
                    // 目前限定一个规则只有一个时间条件
                    TimerCondition timerCondition = timerConditions.get(0);
                    long triggerTime = event.getTimeStamp() + timerCondition.getTimeLate();
                    context.timerService().registerEventTimeTimer(triggerTime);
                    // 在定时信息State中记录
                    ruleTimerState.add(Tuple2.of(rule, triggerTime));
                } else {
                    ResultBean resultBean = new ResultBean(event.getDeviceId(), rule.getRuleId(), event.getTimeStamp(), System.currentTimeMillis());
                    collector.collect(resultBean);
                }
            }
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, DynamicKeyedBean, ResultBean>.OnTimerContext ctx, Collector<ResultBean> out) throws Exception {
        Iterable<Tuple2<MarketingRule, Long>> ruleTimerStateIterable = ruleTimerState.get();
        Iterator<Tuple2<MarketingRule, Long>> iterator = ruleTimerStateIterable.iterator();
        while (iterator.hasNext()) {
            Tuple2<MarketingRule, Long> tp = iterator.next();
            // 判断"规则+定时点"，是否对应本次触发点
            if (tp.f1 == timestamp) {
                // 如果对应，检查该规则的定时条件
                MarketingRule rule = tp.f0;
                TimerCondition timerCondition = rule.getTimerConditions().get(0);
                // 调用service去检查在条件指定的时间范围内，组合事件发生的次数是否满足
                boolean b = triggerModelController.isMatchTimerCondition(ctx.getCurrentKey(), timerCondition,
                        timestamp - timerCondition.getTimeLate(), timestamp);
                // 清楚state中记录的已经检查完毕的定时规则信息
                iterator.remove();
                if (b) {
                    ResultBean resultBean = new ResultBean(ctx.getCurrentKey(), rule.getRuleId(), timestamp, System.currentTimeMillis());
                    out.collect(resultBean);
                }
            }

            // 增加删除state中过期定时信息的逻辑
            if (tp.f1 < timestamp) {
                iterator.remove();
            }
        }
    }
}
