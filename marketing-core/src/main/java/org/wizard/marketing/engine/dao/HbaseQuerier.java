package org.wizard.marketing.engine.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

import java.util.Map;
import java.util.Set;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:11 下午
 * @Desc: Hbase查询器
 */
@Slf4j
public class HbaseQuerier {
    Table table;
    String family;

    public HbaseQuerier(Connection conn, String profileTable, String family) throws Exception {
        table = conn.getTable(TableName.valueOf(profileTable));
        this.family = family;
    }

    /**
     * 从hbase中查询画像条件是否满足
     *
     * @param profileConditions 画像条件 {标签名->标签值，标签名->标签值,........}
     * @param deviceId          账户ID
     * @return 是否匹配
     * @throws Exception 异常
     */
    public boolean queryProfileConditionIsMatch(Map<String, String> profileConditions, String deviceId) throws Exception {
        // 设置行键
        Get get = new Get(deviceId.getBytes());
        // 获取画像条件的标签key
        Set<String> tags = profileConditions.keySet();
        // 封装列
        for (String tag : tags) {
            get.addColumn(family.getBytes(), tag.getBytes());
        }
        // 执行查询
        Result result = table.get(get);
        for (String tag : tags) {
            byte[] bytes = result.getValue(family.getBytes(), tag.getBytes());
            String value = null;
            if (bytes != null) {
                value = new String(bytes);
            }
            log.debug("规则画像条件标签: [{}: {}], 查询到的标签为: [{}: {}]}", tag, profileConditions.get(tag), tag, value);
            if (StringUtils.isBlank(value) || !profileConditions.get(tag).equals(value)) return false;
        }
        return true;
    }
}
