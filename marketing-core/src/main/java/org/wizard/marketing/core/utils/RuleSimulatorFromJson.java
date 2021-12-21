package org.wizard.marketing.core.utils;

import org.apache.commons.io.FileUtils;
import org.wizard.marketing.core.beans.MarketingRule;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/20 11:50 AM
 * @Desc: 规则Json解析
 */
public class RuleSimulatorFromJson {
    public static List<MarketingRule> getRule() throws IOException {
        String json2 = FileUtils.readFileToString(new File("rules/rule2.json"), "utf-8");
        String json1 = FileUtils.readFileToString(new File("rules/rule1.json"), "utf-8");
        MarketingRule rule1 = JsonParseUtils.parseObject(json1, MarketingRule.class);
        MarketingRule rule2 = JsonParseUtils.parseObject(json2, MarketingRule.class);

        return Arrays.asList(rule1, rule2);
    }
}
