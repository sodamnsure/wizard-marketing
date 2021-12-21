package org.wizard.marketing.core.utils;

import org.wizard.marketing.core.beans.CombCondition;
import org.wizard.marketing.core.beans.Condition;
import org.wizard.marketing.core.beans.MarketingRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/3 2:43 下午
 * @Desc: 规则模拟器
 */
public class RuleMonitor {
    public static MarketingRule getRule() {
        MarketingRule rule = new MarketingRule();
        rule.setRuleId("rule_001");
        rule.setKeyByFields("deviceId");

        // 设置触发事件
        HashMap<String, String> map = new HashMap<>();
        map.put("p2", "v1");
        Condition triggerCondition = new Condition("K", map, -1, Long.MAX_VALUE, 1, 999);
        rule.setTriggerCondition(triggerCondition);

        // 画像条件
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("k1", "v1");
        rule.setProfileConditions(map1);

        // 单个行为次数条件列表
        String eventId = "S";
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("p1", "v6");
        map2.put("p7", "v1");
        long startTime = -1;
        long endTime = Long.MAX_VALUE;
        String sql1 = "select eventId\n" +
                "from default.event_detail\n" +
                "where eventId = 'S'\n" +
                "  and properties['p1'] = 'v6'\n" +
                "  and properties['p7'] = 'v1'\n" +
                "  and deviceId = ?\n" +
                "  and timeStamp between ? and ?";
        String rPattern1 = "(1)";
        Condition e = new Condition(eventId, map2, startTime, endTime, 1, 999);
        CombCondition combCondition1 = new CombCondition(startTime, endTime, Collections.singletonList(e), rPattern1, 1, 999, "ck", sql1, "001");

        // 行为组合条件
        long st = -1;
        long ed = Long.MAX_VALUE;

        String eventId1 = "A";
        HashMap<String, String> map3 = new HashMap<>();
        map3.put("p3", "v2");
        Condition e1 = new Condition(eventId1, map3, st, ed, 1, 999);

        String eventId2 = "C";
        HashMap<String, String> map4 = new HashMap<>();
        map3.put("p3", "v2");
        Condition e2 = new Condition(eventId2, map4, st, ed, 1, 999);

        String eventId3 = "F";
        HashMap<String, String> map5 = new HashMap<>();
        map3.put("p3", "v2");
        Condition e3 = new Condition(eventId3, map5, st, ed, 1, 999);

        String sql2 = "select eventId\n" +
                "from default.event_detail\n" +
                "where deviceId = ?\n" +
                "and timeStamp between ? and ?\n" +
                "and (eventId = 'A' or eventId = 'C' or eventId = 'F')";
        String rPattern2 = "(1.*2.*3)";
        CombCondition combCondition2 = new CombCondition(st, ed, Arrays.asList(e1, e2, e3), rPattern2, 1, 999, "ck", sql2, "002");

        rule.setActionConditions(Arrays.asList(combCondition1, combCondition2));

        return rule;
    }

    public static void main(String[] args) {
        MarketingRule rule = getRule();
        String json = JsonParseUtils.toJsonString(rule);
        System.out.println(json);

    }
}
