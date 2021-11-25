package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/10/30 3:47 下午
 * @Desc: 规则条件中，最基本的组成单元，封装着对一个事件的约束
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    /**
     * 条件中要求一个事件的ID
     */
    private String eventId;

    /**
     * 条件中要求一个事件的属性约束
     */
    private Map<String, String> eventProps;

    /**
     * 条件中要求一个事件发生的时间段的起始时间
     */
    private long timeRangeStart;

    /**
     * 条件中要求一个事件发生的时间段的结束时间
     */
    private long timeRangeEnd;

    /**
     * 条件中要求一个事件发生的次数最小值
     */
    private int minLimit;

    /**
     * 条件中要求一个事件发生的次数最大值
     */
    private int maxLimit;
}
