package org.wizard.marketing.core.functions;

import org.apache.flink.api.common.functions.MapFunction;
import org.wizard.marketing.core.beans.Event;
import org.wizard.marketing.core.utils.JsonUtils;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 2:54 下午
 * @Desc: Json转Bean对象
 */
public class JsonToBeanMapFunction implements MapFunction<String, Event> {
    @Override
    public Event map(String json) {
        return JsonUtils.parseObject(json, Event.class);
    }
}
