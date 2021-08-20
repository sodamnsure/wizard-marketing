package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/20 11:43 上午
 * @Desc: 基本条件三要素：事件ID、事件属性、事件阈值
 */
@Data
@AllArgsConstructor
public class UnitCondition {
    private String eventId;
    private Map<String, String> properties;
    private Integer threshold;
    private Long startTime;
    private Long endTime;
}
