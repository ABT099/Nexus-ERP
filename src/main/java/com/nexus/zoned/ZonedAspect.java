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
public class ZonedAspect {

    private final ZoneFilter zoneFilter;
    private final TimeZoneHolder timeZoneHolder;

    public ZonedAspect(ZoneFilter zoneFilter, TimeZoneHolder timeZoneHolder) {
        this.zoneFilter = zoneFilter;
        this.timeZoneHolder = timeZoneHolder;
    }

    @Pointcut("@annotation(Zoned)")
    public void zonedMethod() {}

    @Before("zonedMethod()")
    public void applyZoneBeforeMethod(JoinPoint joinPoint) {
        zoneFilter.setApplyFilter(true);
        ZoneId targetZone = timeZoneHolder.getTimeZone();

        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            Object arg = joinPoint.getArgs()[i];
            if (arg instanceof ZonedDateTime original) {
                joinPoint.getArgs()[i] = original.withZoneSameInstant(targetZone);
            }
        }
    }

    @After("zonedMethod()")
    public void clearZoneAfterMethod() {
        zoneFilter.setApplyFilter(false);
    }
}
