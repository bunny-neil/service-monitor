package com.neil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class TCPConnectionTaskStatusListenerScheduler implements Runnable
{
    private TCPConnectionMonitor monitor;
    private ScheduledExecutorService listenerScheduler;
    private ScheduledExecutorService graceExpiresListenerScheduler;
    private List<TCPConnectionStatusListenerWrapper> listenerList;
    private long graceDurationInMillis = -1L;

    public TCPConnectionTaskStatusListenerScheduler(TCPConnectionMonitor monitor)
    {
        this.monitor = monitor;
        listenerScheduler = createListenerScheduler();
        graceExpiresListenerScheduler = createListenerScheduler();
        listenerList = createListenerList();
    }

    public void scheduleListener(TCPConnectionStatusListener listener, long frequency, TimeUnit unit)
    {
        listenerList.add(new TCPConnectionStatusListenerWrapper(listener, frequency, unit));
        listenerScheduler.scheduleAtFixedRate(() -> monitor.registerListener(listener), frequency, frequency, unit);
    }

    public void scheduleListenersOnGraceExpires(LocalDateTime start, LocalDateTime end)
    {
        LocalDateTime now = LocalDateTime.now();
        graceDurationInMillis = toMilliseconds(end) - toMilliseconds(start);

        long delay = toMilliseconds(end) - toMilliseconds(now);
        graceExpiresListenerScheduler.schedule(this, delay, TimeUnit.MILLISECONDS);
    }

    public void shutdown()
    {
        listenerScheduler.shutdown();
        graceExpiresListenerScheduler.shutdown();
    }

    @Override
    public void run()
    {
        if (graceDurationInMillis > 0) {
            Set<TCPConnectionStatusListener> matchedWrappers = listenerList.stream()
                    .filter(wrapper -> wrapper.getFrequencyInMillis() > graceDurationInMillis)
                    .collect(Collectors.toSet());
            if (matchedWrappers.size() > 0)
            {
                monitor.notifyListeners(monitor.checkConnectionImmediately(), matchedWrappers);
            }
        }
    }

    protected ScheduledExecutorService createListenerScheduler()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }

    protected List<TCPConnectionStatusListenerWrapper> createListenerList()
    {
        return new CopyOnWriteArrayList<>();
    }

    private long toMilliseconds(LocalDateTime dateTime)
    {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
