package org.wizard.marketing.core.service.query;

import org.apache.flink.api.common.state.ListState;
import org.wizard.marketing.core.beans.ConditionBean;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.SequenceConditionBean;
import org.wizard.marketing.core.common.operators.CompareOperator;

import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/27 4:44 下午
 * @Desc:
 */
public class StateQueryServiceImpl {
    ListState<EventBean> eventListState;

    public StateQueryServiceImpl(ListState<EventBean> eventListState) {
        this.eventListState = eventListState;
    }

    public int queryEventSequence(List<ConditionBean> conditions, Long startTime, Long endTime) throws Exception {
        int i = 0;

        int count = 0;
        Iterable<EventBean> eventIterable = eventListState.get();
        for (EventBean eventInState : eventIterable) {
            if (eventInState.getTimeStamp() >= startTime &&
                    eventInState.getTimeStamp() <= endTime &&
                    CompareOperator.compareUnit(conditions.get(i), eventInState)) {
                count++;
                i++;
                if (i == conditions.size()) return count;
            }
        }

        return count;
    }
}
