package org.wizard.marketing.core.controller;

import org.apache.flink.api.common.state.ListState;
import org.wizard.marketing.core.beans.Condition;
import org.wizard.marketing.core.beans.CombCondition;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.MarketingRule;
import org.wizard.marketing.core.service.TriggerModelServiceImpl;
import org.wizard.marketing.core.utils.EventUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/9 10:29 上午
 * @Desc: 触发形规则模型Controller层
 */
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
        // 判断是否满足画像条件
        Map<String, String> profileConditions = rule.getProfileConditions();
        if (profileConditions != null && profileConditions.size() > 0) {
            boolean b = triggerModelService.matchProfileCondition(profileConditions, event.getDeviceId());
            if (!b) return false;
        }

        // 判断是否满足组合条件
        List<CombCondition> actionConditions = rule.getActionConditions();
        if (actionConditions != null && actionConditions.size() > 0) {
            // 一次取一个"组合条件"进行计算
            for (CombCondition combCondition : actionConditions) {
                boolean b = triggerModelService.matchCombCondition(event, combCondition);
                // 后面会使用规则系统动态判断多个组合条件之间的且与或关系
                if (!b) return false;
            }
        }
        return true;
    }
}
