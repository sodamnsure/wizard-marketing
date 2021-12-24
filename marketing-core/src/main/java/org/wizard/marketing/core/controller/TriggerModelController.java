package org.wizard.marketing.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ListState;
import org.wizard.marketing.core.beans.*;
import org.wizard.marketing.core.service.TriggerModelServiceImpl;
import org.wizard.marketing.core.utils.EventUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/9 10:29 上午
 * @Desc: 触发形规则模型Controller层
 */
@Slf4j
public class TriggerModelController {
    TriggerModelServiceImpl triggerModelService;

    /**
     * 构造函数
     *
     * @param listState 状态list
     * @throws Exception 异常
     */
    public TriggerModelController(ListState<EventBean> listState) throws Exception {
        triggerModelService = new TriggerModelServiceImpl(listState);
    }

    /**
     * 判断是否匹配营销规则
     *
     * @param rule  营销规则
     * @param event 事件
     * @return 规则是否匹配
     */
    public boolean ruleIsMatch(MarketingRule rule, EventBean event) throws Exception {
        Condition triggerCondition = rule.getTriggerCondition();
        // 判断当前事件是否满足规则的触发条件
        if (!EventUtils.eventMatchCondition(event, triggerCondition)) return false;

        log.debug("规则: {}, 分组依据: {}, 分组key: {}, 满足触发条件", rule.getRuleId(), rule.getKeyByFields(), event.getKeyByValue());
        // 判断是否满足画像条件
        Map<String, String> profileConditions = rule.getProfileConditions();
        if (profileConditions != null && profileConditions.size() > 0) {
            // 画像条件在动态keyBy中，只有用户ID作为key的时候，才会存在，所以此时的 `event.getKeyByValue()` 等于 `event.getDeviceId()`
            boolean b = triggerModelService.matchProfileCondition(profileConditions, event.getKeyByValue());
            log.debug("规则: {}, 分组依据: {}, 分组key: {}, 画像条件匹配结果: {}", rule.getRuleId(), rule.getKeyByFields(), event.getKeyByValue(), b);
            if (!b) return false;
        }

        // 判断是否满足组合条件
        List<CombCondition> actionConditions = rule.getActionConditions();
        if (actionConditions != null && actionConditions.size() > 0) {
            // 一次取一个"组合条件"进行计算
            for (CombCondition combCondition : actionConditions) {
                boolean b = triggerModelService.matchCombCondition(event, combCondition);
                log.debug("规则: {}, 分组依据: {}, 分组key: {}, 事件组合条件: {}, 计算结果: {}",
                        rule.getRuleId(), rule.getKeyByFields(), event.getKeyByValue(), combCondition, b);
                // 后面会使用规则系统动态判断多个组合条件之间的且与或关系
                if (!b) return false;
            }
        }
        return true;
    }

    /**
     * 检查定时条件是否满足
     *
     * @param keyByValue      分组key
     * @param timerCondition  定时条件
     * @param queryRangeStart 起始时间
     * @param queryRangeEnd   结束时间
     * @return 定时条件是否匹配
     */
    public boolean isMatchTimerCondition(String keyByValue, TimerCondition timerCondition, long queryRangeStart, long queryRangeEnd) throws Exception {
        List<CombCondition> actionConditions = timerCondition.getActionConditions();
        for (CombCondition combCondition : actionConditions) {
            combCondition.setTimeRangeStart(queryRangeStart);
            combCondition.setTimeRangeEnd(queryRangeEnd);

            // 因为service的条件计算方法中，需要知道查询keyByValue，还需要一个时间戳来计算分界点
            EventBean eventBean = new EventBean();
            eventBean.setKeyByValue(keyByValue);
            eventBean.setTimeStamp(queryRangeEnd);

            boolean b = triggerModelService.matchCombCondition(eventBean, combCondition);
            if (!b) return false;
        }
        return true;
    }
}
