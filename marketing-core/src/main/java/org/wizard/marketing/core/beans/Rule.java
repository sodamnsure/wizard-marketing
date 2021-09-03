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
public class Rule {
    // 规则ID
    private String ruleId;

    // 触发条件
    private Condition triggerEvent;

    // 画像属性条件
    private Map<String, String> profileConditions;

    // 行为次数条件
    private List<Condition> actionCountConditions;

    // 行为次序条件
    private List<Condition> actionSequenceConditions;
}
