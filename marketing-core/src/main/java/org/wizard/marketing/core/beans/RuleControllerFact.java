package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wizard.marketing.core.service.TriggerModelServiceImpl;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/28 2:04 PM
 * @Desc: 用于向drools的KieSession插入的fact对象封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleControllerFact {
    /**
     * 营销规则
     */
    private MarketingRule marketingRule;

    /**
     * 事件源
     */
    private EventBean eventBean;

    /**
     * 查询服务
     */
    private TriggerModelServiceImpl triggerModelService;

    /**
     * 预留匹配结果
     */
    private boolean matchResult;
}
