package com.nexus.zoned;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.ZoneId;

@Component
@RequestScope
public class TimeZoneHolder {
    private ZoneId timeZone;

    public ZoneId getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }
}
