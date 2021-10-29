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

    /*
     行为序列条件目前的缺陷:
        只支持.*A.*B.*C.* 这种类型(意味着ABC三种行为中间)
     */
    private List<SequenceConditionBean> sequenceConditions;
}
