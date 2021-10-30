package org.wizard.marketing.engine.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/10/30 3:47 下午
 * @Desc: 规则条件中，最原子的一个封装，封装一个事件条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCondition {
    /**
     * 规则条件中的一个事件的ID
     */
    private String eventId;

    /**
     * 规则条件中的一个事件的属性约束
     */
    private Map<String, String> eventProps;

    /**
     * 规则条件中的一个事件要求的发生时间段起始
     */
    private long timeRangeStart;

    /**
     * 规则条件中的一个事件要求的发生时间段结束
     */
    private long timeRangeEnd;

    /**
     * 规则条件中的一个事件要求的发生次数最小值
     */
    private int minLimit;

    /**
     * 规则条件中的一个事件要求的发生次数最大值
     */
    private int maxLimit;
}
