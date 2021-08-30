package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 7:17 下午
 * @Desc: 规则匹配计算结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Result {
    private String deviceId;
    private String ruleId;
    private Long triggerTime;
    private Long processTime;
}
