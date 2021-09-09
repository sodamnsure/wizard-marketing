package org.wizard.marketing.core.utils;

import org.wizard.marketing.core.beans.ConditionBean;
import org.wizard.marketing.core.beans.RuleBean;

import java.util.Collections;
import java.util.HashMap;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/3 2:43 下午
 * @Desc: 规则模拟器
 */
public class RuleMonitor {
    public static RuleBean getRule() {
        // 创建规则
        RuleBean rule = new RuleBean();

        rule.setRuleId("test_rule1");

        // 触发条件
        ConditionBean trigger = new ConditionBean();
        HashMap<String, String> triggerProp = new HashMap<>();
        triggerProp.put("p1", "v1");
        trigger.setEventId("K");
        trigger.setProperties(triggerProp);
        trigger.setThreshold(0);
        rule.setTriggerEvent(trigger);

        // 画像属性条件
        HashMap<String, String> userProfile = new HashMap<>();
        userProfile.put("k1", "v2");
        rule.setProfileConditions(userProfile);

        // 行为次数条件
        ConditionBean actionCount = new ConditionBean();
        actionCount.setEventId("S");
        HashMap<String, String> actionProp = new HashMap<>();
        actionProp.put("p1", "v6");
        actionProp.put("p7", "v1");
        actionCount.setProperties(actionProp);
        actionCount.setThreshold(1);
        Long startTime = 1630652600000L;
        actionCount.setStartTime(startTime);
        Long endTime = Long.MAX_VALUE;
        actionCount.setEndTime(endTime);

        String sql = "" +
                "select " +
                "count(*) as cnt\n" +
                "from default.event_detail\n" +
                "where eventId = 'S'\n" +
                "  and properties['p1'] = 'v6'\n" +
                "  and properties['p7'] = 'v1'\n" +
                "  and deviceId = ?\n" +
                "  and timeStamp between " + startTime + " and " + endTime;

        actionCount.setQuerySql(sql);

        rule.setCountConditions(Collections.singletonList(actionCount));

        // 行为序列条件
        ConditionBean seq1 = new ConditionBean();
        HashMap<String, String> seqProp1 = new HashMap<>();
        seqProp1.put("p3", "v3");
        seq1.setEventId("A");
        seq1.setProperties(seqProp1);

        ConditionBean seq2 = new ConditionBean();
        HashMap<String, String> seqProp2 = new HashMap<>();
        seqProp2.put("p1", "v1");
        seq2.setEventId("C");
        seq2.setProperties(seqProp2);

        ConditionBean seq3 = new ConditionBean();
        HashMap<String, String> seqProp3 = new HashMap<>();
        seqProp3.put("p5", "v1");
        seq3.setEventId("F");
        seq3.setProperties(seqProp3);



        return rule;
    }
}
