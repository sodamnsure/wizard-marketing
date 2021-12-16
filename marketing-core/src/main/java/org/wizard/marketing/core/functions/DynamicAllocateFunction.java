package org.wizard.marketing.core.functions;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.wizard.marketing.core.beans.DynamicKeyedBean;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.MarketingRule;
import org.wizard.marketing.core.utils.RuleMonitor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/15 7:50 PM
 * @Desc: 方便后续动态KeyBy的数据复制方法
 */
public class DynamicAllocateFunction extends ProcessFunction<EventBean, DynamicKeyedBean> {
    HashSet<String> keyByFieldsSet;

    @Override
    public void open(Configuration parameters) {
        // 获取规则中所有的规则列表
        MarketingRule rule1 = RuleMonitor.getRule();
        MarketingRule rule2 = RuleMonitor.getRule();
        rule2.setKeyByFields("ip");

        // 从规则列表中遍历每个规则，获取每个规则的keyBy字段，并放入set集合去重
        List<MarketingRule> ruleList = Arrays.asList(rule1, rule2);
        keyByFieldsSet = new HashSet<>();
        for (MarketingRule rule : ruleList) {
            keyByFieldsSet.add(rule.getKeyByFields());
        }
    }

    @Override
    public void processElement(EventBean eventBean, Context context, Collector<DynamicKeyedBean> collector) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String keyByFields : keyByFieldsSet) {
            String[] fieldNames = keyByFields.split(",");
            // 拼装keyByFields中指定的每一个字段的值
            for (String fieldName : fieldNames) {
                Class<?> beanClass = Class.forName("org.wizard.marketing.core.beans.EventBean");
                Field declaredField = beanClass.getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                String fieldValue = (String) declaredField.get(eventBean);

                sb.append(fieldValue).append(",");
            }
            DynamicKeyedBean dynamicKeyedBean = new DynamicKeyedBean(sb.substring(0, sb.length() - 1), keyByFields, eventBean);
            collector.collect(dynamicKeyedBean);
        }
    }
}