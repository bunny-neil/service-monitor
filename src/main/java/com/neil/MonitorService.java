package com.neil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitorService
{
    public static final String ADDRESS_PORT_SEPARATOR = "/";

    private Map<String, ScheduledExecutorService> servicesAndSchedulers = new HashMap<>();
    private Map<String, TCPConnectionMonitor> servicesAndMonitors = new HashMap<>();

    public void register(String service, long frequency, TimeUnit unit, TCPConnectionStatusListener listener)
    {
        TCPConnectionMonitor monitor = getOrCreateMonitor(service);
        monitor.start();

        ScheduledExecutorService scheduler = getOrCreateScheduler(service);
        scheduler.scheduleAtFixedRate(() -> monitor.registerListener(listener), frequency, frequency, unit);
    }

    public void scheduleOutage(String service, LocalDateTime startDate, LocalDateTime endDate)
    {
        TCPConnectionMonitor monitor = servicesAndMonitors.get(service);
        monitor.scheduleOutage(startDate, endDate);
    }

    public void shutdown()
    {
        servicesAndSchedulers.values().forEach(ScheduledExecutorService::shutdown);
        servicesAndMonitors.values().forEach(TCPConnectionMonitor::shutdown);
    }

    private TCPConnectionMonitor getOrCreateMonitor(String service)
    {
        return servicesAndMonitors.computeIfAbsent(service, s -> new TCPConnectionMonitor(parseAddress(s), parsePort(s)));
    }

    private ScheduledExecutorService getOrCreateScheduler(String service)
    {
        return servicesAndSchedulers.computeIfAbsent(service, k -> Executors.newSingleThreadScheduledExecutor());
    }

    private String parseAddress(String service)
    {
        return service.substring(0, service.indexOf(ADDRESS_PORT_SEPARATOR));
    }

    private int parsePort(String service)
    {
        String portStr = service.substring(service.indexOf(ADDRESS_PORT_SEPARATOR) + 1);
        return Integer.parseInt(portStr);
    }
}
