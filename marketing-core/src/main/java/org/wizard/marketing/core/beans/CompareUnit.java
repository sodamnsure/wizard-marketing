package org.wizard.marketing.core.beans;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/20 4:59 下午
 * @Desc: 比较单元
 */
public abstract class CompareUnit {
    private String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    private Map<String, String> properties;
}
