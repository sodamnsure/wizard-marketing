package org.wizard.marketing.engine.functions;

import org.apache.flink.api.common.functions.MapFunction;
import org.wizard.marketing.engine.beans.EventBean;
import org.wizard.marketing.engine.utils.JsonParseUtils;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 2:54 下午
 * @Desc: Json转Bean对象
 */
public class JsonToBeanFunction implements MapFunction<String, EventBean> {
    @Override
    public EventBean map(String json) {
        return JsonParseUtils.parseObject(json, EventBean.class);
    }
}
