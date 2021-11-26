package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/26 10:47 上午
 * @Desc: 定时条件封装对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimerCondition {
    /**
     * 定时器时间
     */
    private long timeLate;

    /**
     * 需要满足满足定时器规则的条件
     */
    private List<CombCondition> actionConditions;
}
