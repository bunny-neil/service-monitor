package com.neil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class TCPConnectionTaskStatusListenerScheduler
{
    private ScheduledExecutorService listenerScheduler;
    private List<TCPConnectionStatusListener> listenerList;

    public TCPConnectionTaskStatusListenerScheduler()
    {
        listenerScheduler = createListenerScheduler();
        listenerList = createListenerList();
    }

    public void scheduleListener(TCPConnectionMonitor monitor, TCPConnectionStatusListener listener, long frequency, TimeUnit unit)
    {
        listenerList.add(listener);
        listenerScheduler.scheduleAtFixedRate(() -> monitor.registerListener(listener), frequency, frequency, unit);
    }

    public void shutdown()
    {
        listenerScheduler.shutdown();
    }

    protected ScheduledExecutorService createListenerScheduler()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }

    protected List<TCPConnectionStatusListener> createListenerList()
    {
        return new ArrayList<>();
    }
}
