package org.wizard.marketing.core.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/9 2:58 下午
 * @Desc: 单个序列条件
 */
@Setter
@Getter
public class SequenceConditionBean {
    private String ruleId;
    private Long startTime;
    private Long endTime;

    private List<ConditionBean> conditions;

    private String sequenceQuerySql;
}
