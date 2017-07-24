package com.neil.example;

import com.neil.MonitorService;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class ServiceMonitorExample
{
    private static final String GOOGLE_SERVICE = "www.google.com/80";
    private static final String GLOBAL_RELAY = "www.globalrelay.com/80";

    public static void main(String...args) throws InterruptedException
    {
        MonitorService monitorService = new MonitorService();

        registerGlobalRelay(monitorService);
        registerGoogleService(monitorService);
        Thread.sleep(15000); // run 15 seconds

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusSeconds(1L);
        LocalDateTime end = start.plusSeconds(20L);
        monitorService.scheduleOutage(GOOGLE_SERVICE, start, end);

        Thread.sleep(15000); // run another 15 seconds
        monitorService.shutdown();
    }

    public static void registerGlobalRelay(MonitorService monitorService)
    {
        ServiceMonitorListener globalRelay3 = new ServiceMonitorListener(
                GLOBAL_RELAY, 3, TimeUnit.SECONDS);
        ServiceMonitorListener globalRelay5 = new ServiceMonitorListener(
                GLOBAL_RELAY, 5, TimeUnit.SECONDS);
        monitorService.register(
                globalRelay3.getService(),
                globalRelay3.getFrequency(),
                globalRelay3.getUnit(),
                globalRelay3);
        monitorService.register(
                globalRelay5.getService(),
                globalRelay5.getFrequency(),
                globalRelay5.getUnit(),
                globalRelay5);
    }

    public static void registerGoogleService(MonitorService monitorService)
    {
        ServiceMonitorListener googleService5 = new ServiceMonitorListener(
                GOOGLE_SERVICE, 5, TimeUnit.SECONDS);
        ServiceMonitorListener googleService8 = new ServiceMonitorListener(
                GOOGLE_SERVICE, 8, TimeUnit.SECONDS);
        monitorService.register(
                googleService5.getService(),
                googleService5.getFrequency(),
                googleService5.getUnit(),
                googleService5);
        monitorService.register(
                googleService8.getService(),
                googleService8.getFrequency(),
                googleService8.getUnit(),
                googleService8);
    }
}
