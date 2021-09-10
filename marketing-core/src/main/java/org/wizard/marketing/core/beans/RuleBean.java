package org.wizard.marketing.core.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:37 下午
 * @Desc: 规则条件封装对象
 */
@Getter
@Setter
public class RuleBean {
    // 规则ID
    private String ruleId;

    // 触发条件
    private ConditionBean triggerEvent;

    // 画像条件
    private Map<String, String> profileConditions;

    // 次数条件
    private List<ConditionBean> countConditions;

    // 序列条件
    private List<SequenceConditionBean> sequenceConditions;
}
