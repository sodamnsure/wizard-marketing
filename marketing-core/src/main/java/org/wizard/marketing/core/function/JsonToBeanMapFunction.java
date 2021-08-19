package org.wizard.marketing.core.function;

import org.apache.flink.api.common.functions.MapFunction;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.utils.JsonUtils;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 2:54 下午
 * @Desc: Json转Bean对象
 */
public class JsonToBeanMapFunction implements MapFunction<String, EventBean> {
    @Override
    public EventBean map(String json) throws Exception {
        return JsonUtils.parseObject(json, EventBean.class);
    }
}
