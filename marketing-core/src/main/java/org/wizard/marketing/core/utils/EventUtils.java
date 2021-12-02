package org.wizard.marketing.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.Condition;
import org.wizard.marketing.core.beans.EventBean;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 5:20 下午
 * @Desc: 事件工具类
 */
@Slf4j
public class EventUtils {
    public static int eventSeqStrMatchRegexCount(String eventSeqStr, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(eventSeqStr);
        int count = 0;
        while (matcher.find()) count++;
        log.debug("字符串正则匹配，正则表达式为: {}, 匹配结果为: {}, 字符串为: {}", pattern, count, eventSeqStr);
        return count;
    }

    public static boolean eventMatchCondition(EventBean event, Condition condition) {
        if (event.getEventId().equals(condition.getEventId())) {
            Set<String> keySet = condition.getEventProps().keySet();
            for (String key : keySet) {
                String conditionValue = condition.getEventProps().get(key);
                // null调用equals方法，空指针异常，一般 [常量.equals(变量)]
                if (!conditionValue.equals(event.getProperties().get(key))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
