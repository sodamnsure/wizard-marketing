package org.wizard.marketing.core.common.operators;

import org.wizard.marketing.core.beans.basic.Unit;

import java.util.Set;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/20 3:45 下午
 * @Desc: 比较器
 */
public class CompareOperator {
    /**
     * 比较规则中的条件是否匹配
     */
    public boolean compareUnit(Unit unit1, Unit unit2) {
        if (unit1.getEventId().equals(unit2.getEventId())) {
            Set<String> keys = unit1.getProperties().keySet();
            for (String key : keys) {
                String value = unit2.getProperties().get(key);
                if (!unit1.getProperties().get(key).equals(value)) return false;
            }
            return true;
        }

        return false;
    }

}
