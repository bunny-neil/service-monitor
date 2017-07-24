package com.neil.example;

import com.neil.MonitorService;

import java.time.LocalDateTime;

public class MultipleServicesWithGraceExample
{
    public static void main(String...args) throws InterruptedException
    {
        MonitorService monitorService = new MonitorService();

        ServiceMonitorListenerFactory google20 = ServiceMonitorListenerFactory.createGoogleListener(20);
        monitorService.register(
                google20.getService(),
                google20.getFrequency(),
                google20.getUnit(),
                google20);
        Thread.sleep(10000); // run 10 seconds

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusSeconds(0L);
        LocalDateTime end = start.plusSeconds(5L);
        monitorService.setGraceTime(start, end);
        Thread.sleep(6000); // run 6 seconds

        monitorService.shutdown();
    }
}
