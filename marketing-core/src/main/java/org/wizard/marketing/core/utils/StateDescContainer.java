package org.wizard.marketing.core.utils;

import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.wizard.marketing.core.beans.EventBean;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/17 11:14 上午
 * @Desc: 状态统一管理类
 */
public class StateDescContainer {
    // 默认state存储时间
    private static final Integer TTL_TIME = 2;

    /**
     * 近期行为事件存储状态描述
     */
    public static ListStateDescriptor<EventBean> getEventBeansDesc() {
        ListStateDescriptor<EventBean> eventBeansDesc = new ListStateDescriptor<>("event_beans", EventBean.class);
        StateTtlConfig stateTtlConfig = StateTtlConfig.newBuilder(Time.hours(TTL_TIME)).build();
        eventBeansDesc.enableTimeToLive(stateTtlConfig);

        return eventBeansDesc;
    }

}
