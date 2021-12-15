package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/15 11:45 AM
 * @Desc: 用于动态分区的包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicKeyedBean {
    /**
     * 封装的Key的值
     */
    private String keyValue;

    /**
     * 封装的Key的名称
     */
    private String keyNames;

    /**
     * 携带的数据本身
     */
    private EventBean eventBean;
}
