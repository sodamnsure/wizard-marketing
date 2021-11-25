package org.wizard.marketing.core.beans;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/10/30 4:30 下午
 * @Desc: 营销规则
 */
@Data
public class MarketingRule {
    /**
     * 规则ID
     */
    private String ruleId;

    /**
     * 触发条件
     */
    private Condition triggerCondition;

    /**
     * 规则匹配推送次数限制
     */
    private int matchLimit;

    /**
     * 画像条件
     */
    private Map<String, String> profileConditions;

    /**
     * 行为条件
     */
    private List<CombCondition> actionConditions;

    /**
     * 是否要注册timer
     */
    private boolean isOnTimer;
}
