package org.wizard.marketing.core.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/17 3:48 下午
 * @Desc: 分段查询的辅助工具
 */
public class SegmentQueryUtils {
    private static final Long SEG_TIME_OFFSET = 2 * 60 * 60 * 1000L;

    /**
     * 给定时间向上取整，倒退2小时
     */
    public static Long getSegmentPoint(Long timeStamp) {
        Date dt = DateUtils.ceiling(new Date(timeStamp - SEG_TIME_OFFSET), Calendar.HOUR);
        return dt.getTime();
    }
}
