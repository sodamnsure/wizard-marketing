package org.wizard.marketing.engine.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/10/30 4:09 下午
 * @Desc: CombinationCondition 的简写::组合条件封装，例如'事件组合'：[C !W F G](>=2)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombCondition {
    /**
     * 组合条件要求'一堆事件'发生的时间段的起始时间
     */
    private long timeRangeStart;

    /**
     * 组合条件要求'一堆事件'发生的时间段的结束时间
     */
    private long timeRangeEnd;

    /**
     * 组合条件中关心的'一堆事件'列表
     */
    private List<Condition> conditionList;

    /**
     * 组合条件未来计算要用到的正则匹配表达式
     */
    private String matchPattern;

    /**
     * 组合条件要求'一堆事件'发生的最小次数
     */
    private int minLimit;

    /**
     * 组合条件要求'一堆事件'发生的最大次数
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
