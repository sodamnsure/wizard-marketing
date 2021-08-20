package org.wizard.marketing.core.beans.basic;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/20 6:12 下午
 * @Desc: 规则系统基本单元
 */
@Getter
@Setter
public abstract class Unit {
    private String eventId;
    private Map<String, String> properties;
}
