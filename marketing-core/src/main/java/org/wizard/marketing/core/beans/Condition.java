package org.wizard.marketing.core.beans;

import lombok.Getter;
import lombok.Setter;
import org.wizard.marketing.core.beans.basic.Unit;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/20 11:43 上午
 * @Desc: 基本条件三要素：事件ID、事件属性、事件阈值
 */
@Getter
@Setter
public class Condition extends Unit {
    private Integer threshold;
    private Long startTime;
    private Long endTime;
}
