package com.neil.example;

import com.neil.MonitorService;

public class MultipleServicesExample
{
    public static void main(String...args) throws InterruptedException
    {
        MonitorService monitorService = new MonitorService();

        ServiceMonitorListenerFactory globalRelay2 = ServiceMonitorListenerFactory.createGlobalRelayListener(2);
        monitorService.register(
                globalRelay2.getService(),
                globalRelay2.getFrequency(),
                globalRelay2.getUnit(),
                globalRelay2);
        ServiceMonitorListenerFactory google2 = ServiceMonitorListenerFactory.createGoogleListener(2);
        monitorService.register(
                google2.getService(),
                google2.getFrequency(),
                google2.getUnit(),
                google2);
        Thread.sleep(10000); // run 10 seconds


        ServiceMonitorListenerFactory google3 = ServiceMonitorListenerFactory.createGoogleListener(3);
        monitorService.register(
                google3.getService(),
                google3.getFrequency(),
                google3.getUnit(),
                google3);
        ServiceMonitorListenerFactory globalRelay3 = ServiceMonitorListenerFactory.createGlobalRelayListener(3);
        monitorService.register(
                globalRelay3.getService(),
                globalRelay3.getFrequency(),
                globalRelay3.getUnit(),
                globalRelay3);
        Thread.sleep(10000); // run 10 seconds

        monitorService.shutdown();
    }
}
