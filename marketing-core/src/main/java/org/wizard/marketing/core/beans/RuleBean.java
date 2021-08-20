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
    // 触发条件
    private ConditionBean triggerEvent;

    // 画像属性条件
    private Map<String, String> userProfileConditions;

    // 行为次数条件
    private List<ConditionBean> actionCountConditions;

    // 行为次序条件
    private List<ConditionBean> actionSequenceConditions;
}
