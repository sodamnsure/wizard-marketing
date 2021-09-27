package org.wizard.marketing.core.service.query;

import org.apache.flink.api.common.state.ListState;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.SequenceConditionBean;

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

    public int queryEventSequence(SequenceConditionBean sequenceConditionBean) {
        return 0;
    }
}
