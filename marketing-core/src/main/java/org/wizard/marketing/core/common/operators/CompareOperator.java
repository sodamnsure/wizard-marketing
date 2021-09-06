package org.wizard.marketing.core.common.operators;

import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.basic.Unit;

import java.util.Set;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/20 3:45 下午
 * @Desc: 比较器
 */
@Slf4j
public class CompareOperator {
    /**
     * 比较规则中的条件是否匹配
     * 抽象类比较，简化代码
     */
    public static boolean compareUnit(Unit unit1, Unit unit2) {
        log.debug("规则触发事件ID: [{}] 当前真实事件ID: [{}]", unit1.getEventId(), unit2.getEventId());

        if (unit1.getEventId().equals(unit2.getEventId())) {
            log.debug("规则触发事件ID: [{}] 当前真实事件ID: [{}] 相等, 准备比较属性.....", unit1.getEventId(), unit2.getEventId());
            Set<String> keys = unit1.getProperties().keySet();
            for (String key : keys) {
                String value = unit2.getProperties().get(key);
                if (!unit1.getProperties().get(key).equals(value)) {
                    log.debug("规则触发事件中属性要求为[{}: {}] 真实事件中的属性为[{}: {}]", key, unit1.getProperties().get(key), key, value);
                    return false;
                }
            }
            return true;
        }
        log.debug("规则触发事件ID: [{}] 当前真实事件ID: [{}] 不相等, 直接返回false", unit1.getEventId(), unit2.getEventId());

        return false;
    }

}
