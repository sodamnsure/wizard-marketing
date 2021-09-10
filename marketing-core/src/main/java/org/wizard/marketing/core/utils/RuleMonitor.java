package org.wizard.marketing.core.utils;

import org.wizard.marketing.core.beans.ConditionBean;
import org.wizard.marketing.core.beans.SequenceConditionBean;
import org.wizard.marketing.core.beans.RuleBean;

import java.util.Arrays;
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
        Long startTime = 1530652600000L;
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
        seqProp1.put("p3", "v8");
        seq1.setEventId("Z");
        seq1.setProperties(seqProp1);

        ConditionBean seq2 = new ConditionBean();
        HashMap<String, String> seqProp2 = new HashMap<>();
        seqProp2.put("p2", "v6");
        seq2.setEventId("I");
        seq2.setProperties(seqProp2);

        ConditionBean seq3 = new ConditionBean();
        HashMap<String, String> seqProp3 = new HashMap<>();
        seqProp3.put("p8", "v7");
        seq3.setEventId("A");
        seq3.setProperties(seqProp3);

        String seqSql = "select deviceId,\n" +
                "       sequenceMatch('.*(?1).*(?2).*(?3)')(\n" +
                "                     toDateTime(`timeStamp`),\n" +
                "                     eventId = 'Z',\n" +
                "                     eventId = 'I',\n" +
                "                     eventId = 'A'\n" +
                "           ) as is_match3,\n" +
                "       sequenceMatch('.*(?1).*(?2).*')(\n" +
                "                     toDateTime(`timeStamp`),\n" +
                "                     eventId = 'Z',\n" +
                "                     eventId = 'I',\n" +
                "                     eventId = 'A'\n" +
                "           ) as is_match2,\n" +
                "       sequenceMatch('.*(?1).*')(\n" +
                "                     toDateTime(`timeStamp`),\n" +
                "                     eventId = 'Z',\n" +
                "                     eventId = 'I',\n" +
                "                     eventId = 'A'\n" +
                "           ) as is_match1\n" +
                "from default.event_detail\n" +
                "where deviceId = ?'\n" +
                "  and timeStamp between ? and ?\n" +
                "  and (\n" +
                "        (eventId = 'Z' and properties['p3'] = 'v8')\n" +
                "        or (eventId = 'I' and properties['p2'] = 'v6')\n" +
                "        or (eventId = 'A' and properties['p8'] = 'v7')\n" +
                "    )\n" +
                "group by deviceId";

        SequenceConditionBean sequenceConditionBean = new SequenceConditionBean();
        sequenceConditionBean.setRuleId("test_rule1");
        sequenceConditionBean.setStartTime(startTime);
        sequenceConditionBean.setEndTime(endTime);
        sequenceConditionBean.setConditions(Arrays.asList(seq1, seq2, seq3));
        sequenceConditionBean.setSequenceQuerySql(seqSql);

        rule.setSequenceConditions(Collections.singletonList(sequenceConditionBean));

        return rule;
    }
}
