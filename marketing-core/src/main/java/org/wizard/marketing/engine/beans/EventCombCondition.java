package org.wizard.marketing.engine.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/10/30 4:09 下午
 * @Desc: EventCombinationCondition的简写::事件组合体条件封装，例如组合：[C !W F G](>=2)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCombCondition {
    /**
     * 组合条件要求发生的时间段起始
     */
    private long timeRangeStart;

    /**
     * 组合条件要求发生的时间段结束
     */
    private long timeRangeEnd;

    /**
     * 组合条件中关心的事件列表
     */
    private List<EventCondition> eventConditionList;

    /**
     * 组合条件未来计算要用到的正则匹配表达式
     */
    private String matchPattern;

    /**
     * 组合条件要求发生的最小次数
     */
    private int minLimit;

    /**
     * 组合条件要求发生的最大次数
     */
    private int maxLimit;

    /**
     * 用于在数据库中过滤关心事件的sql
     */
    private String sqlType;

    /**
     * 用于在数据库中查询的sql
     */
    private String querySql;

    /**
     * 组合条件缓存ID
     */
    private String cacheId;
}
