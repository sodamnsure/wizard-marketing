package org.wizard.marketing.core.functions;

import org.apache.flink.api.common.functions.MapFunction;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.utils.ParseJsonUtils;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 2:54 下午
 * @Desc: Json转Bean对象
 */
public class JsonToBeanFunction implements MapFunction<String, EventBean> {
    @Override
    public EventBean map(String json) {
        return ParseJsonUtils.parseObject(json, EventBean.class);
    }
}
