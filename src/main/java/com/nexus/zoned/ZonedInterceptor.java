package com.nexus.zoned;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;


@Aspect
@Component
public class ZonedInterceptor {

    private final ZoneFilter zoneFilter;
    private final TimeZoneHolder timeZoneHolder;

    public ZonedInterceptor(ZoneFilter zoneFilter, TimeZoneHolder timeZoneHolder) {
        this.zoneFilter = zoneFilter;
        this.timeZoneHolder = timeZoneHolder;
    }

    @Pointcut("@annotation(Zoned)")
    public void zonedMethod() {}
    @Before("zonedMethod()")
    public void applyZoneBeforeMethod(JoinPoint joinPoint) {
        zoneFilter.setApplyFilter(true);

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ZonedDateTime original) {
                ZoneId targetZone = timeZoneHolder.getTimeZone();
                arg = original.withZoneSameInstant(targetZone);
            }
        }
    }

    @After("zonedMethod()")
    public void clearZoneAfterMethod() {
        zoneFilter.setApplyFilter(false);
    }
}
