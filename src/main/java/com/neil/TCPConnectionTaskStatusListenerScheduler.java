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
    private long graceDurationInMillis = 0L;

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
        Set<TCPConnectionStatusListener> matchedListeners = findListenersWithFrequencyGreaterThanGraceDuration();
        if (matchedListeners.size() > 0)
        {
            monitor.notifyListeners(monitor.checkConnectionImmediately(), matchedListeners);
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

    private Set<TCPConnectionStatusListener> findListenersWithFrequencyGreaterThanGraceDuration()
    {
        return listenerList.stream()
                .filter(wrapper -> wrapper.getFrequencyInMillis() > graceDurationInMillis)
                .collect(Collectors.toSet());
    }

    private long toMilliseconds(LocalDateTime dateTime)
    {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
