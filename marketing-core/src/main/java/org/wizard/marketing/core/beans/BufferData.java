package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/7 10:05 AM
 * @Desc: Buffer封装对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BufferData {
    /**
     * 账号
     */
    private String keyByValue;

    /**
     * 缓存ID
     */
    private String cacheId;

    /**
     * 封装的Set集合
     */
    private Map<String, String> valueMap;
}
