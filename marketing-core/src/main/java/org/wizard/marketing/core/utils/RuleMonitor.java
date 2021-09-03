package org.wizard.marketing.core.utils;

import org.wizard.marketing.core.beans.Condition;
import org.wizard.marketing.core.beans.Rule;

import java.util.Collections;
import java.util.HashMap;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/3 2:43 下午
 * @Desc: 规则模拟器
 */
public class RuleMonitor {
    public static Rule getRule() {
        // 创建规则
        Rule rule = new Rule();

        // 触发条件
        Condition trigger = new Condition();
        HashMap<String, String> triggerProp = new HashMap<>();
        triggerProp.put("p1", "v1");
        trigger.setEventId("K");
        trigger.setProperties(triggerProp);
        trigger.setThreshold(0);
        rule.setTriggerEvent(trigger);

        // 画像属性条件
        HashMap<String, String> userProfile = new HashMap<>();
        userProfile.put("k1", "v2");
        userProfile.put("k2", "v2");
        rule.setUserProfileConditions(userProfile);

        // 行为次数条件
        Condition actionCount = new Condition();
        actionCount.setEventId("U");
        HashMap<String, String> actionProp = new HashMap<>();
        actionProp.put("k2", "v2");
        actionProp.put("k3", "v2");
        actionCount.setProperties(actionProp);
        actionCount.setThreshold(2);
        actionCount.setStartTime(1630652600000L);
        actionCount.setEndTime(Long.MAX_VALUE);
        rule.setActionCountConditions(Collections.singletonList(actionCount));

        return rule;
    }
}
