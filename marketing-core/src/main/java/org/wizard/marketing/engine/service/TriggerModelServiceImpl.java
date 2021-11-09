package org.wizard.marketing.engine.service;

import org.wizard.marketing.engine.beans.CombCondition;
import org.wizard.marketing.engine.beans.EventBean;
import org.wizard.marketing.engine.utils.CrossTimeQueryUtils;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:09 下午
 * @Desc: 触发性规则模型查询服务
 */
public class TriggerModelServiceImpl {
    /**
     * 计算单个行为组合条件是否匹配
     */
    public boolean matchCombCondition(EventBean event, CombCondition combCondition) {
        long segmentPoint = CrossTimeQueryUtils.getSegmentPoint(event.getTimeStamp());
        // 判断条件的时间区间是否跨分界点
        long timeRangeStart = combCondition.getTimeRangeStart();
        long timeRangeEnd = combCondition.getTimeRangeEnd();
        if (timeRangeStart >= segmentPoint) {
            // 查状态
        } else if (timeRangeEnd < segmentPoint) {
            // 查ClickHouse
        } else {
            // 跨界查询
        }
    }

}
