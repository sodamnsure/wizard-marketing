package org.wizard.marketing.engine.utils;

import org.wizard.marketing.engine.beans.Condition;
import org.wizard.marketing.engine.beans.EventBean;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 5:20 下午
 * @Desc: 事件工具类
 */
public class EventUtils {
    public static String eventSeqToStr(Iterator<EventBean> eventSeq, List<Condition> conditionList) {
        StringBuilder sb = new StringBuilder();
        while (eventSeq.hasNext()) {
            EventBean next = eventSeq.next();
            for (int i = 0; i < conditionList.size(); i++) {
                if (eventMatchCondition(next, conditionList.get(i - 1))) {
                    sb.append(i);
                }
            }
        }
        return sb.toString();
    }


    public static int eventSeqStrMatchRegexCount(String eventSeqStr, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(eventSeqStr);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }

    public static boolean eventMatchCondition(EventBean event, Condition condition) {
        if (event.getEventId().equals(condition.getEventId())) {
            Set<String> keySet = condition.getEventProps().keySet();
            for (String key : keySet) {
                String conditionValue = condition.getEventProps().get(key);
                if (!event.getProperties().get(key).equals(conditionValue)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
