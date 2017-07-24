package com.neil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class TCPConnectionMonitor implements Runnable
{
    private String address;
    private int port;
    private BlockingQueue<TCPConnectionStatusListener> listenerQueue;
    private ScheduledExecutorService connectionAttemptExecutor;
    private ScheduledExecutorService outagePlanExecutor;
    private boolean inScheduledOutage = false;

    public TCPConnectionMonitor(String address, int port)
    {
        this.address = address;
        this.port = port;
        this.listenerQueue = createListenerQueue();
        this.connectionAttemptExecutor = createSingleThreadScheduleExecutor();
        this.outagePlanExecutor = createSingleThreadScheduleExecutor();
    }

    public void start()
    {
        connectionAttemptExecutor.scheduleWithFixedDelay(this, 0L, 1L, TimeUnit.SECONDS);
    }

    public void shutdown()
    {
        connectionAttemptExecutor.shutdown();
        outagePlanExecutor.shutdown();
    }

    public void registerListener(TCPConnectionStatusListener listener)
    {
        listenerQueue.add(listener);
    }

    public void scheduleOutage(LocalDateTime startDate, LocalDateTime endDate)
    {
        LocalDateTime now = LocalDateTime.now();
        long startDelay = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                - now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endDelay = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                - now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        outagePlanExecutor.schedule(() -> {inScheduledOutage = true; System.out.println("start outage for service" + address + "/" + port);}, startDelay, TimeUnit.MILLISECONDS);
        outagePlanExecutor.schedule(() -> {inScheduledOutage = false; System.out.println("end outage for service" + address + "/" + port);}, endDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run()
    {
        final boolean inScheduledOutageCopy = inScheduledOutage;
        if (listenerQueue.size() > 0) {
            TCPConnectionTask task = new TCPConnectionTask(address, port);
            TCPConnectionStatus newStatus = task.run();
            Set<TCPConnectionStatusListener> listenerSet = drainListeners();
            if ( ! inScheduledOutageCopy) {
                notifyListeners(newStatus, listenerSet);
            }
        }
    }

    protected Set<TCPConnectionStatusListener> drainListeners()
    {
        Set<TCPConnectionStatusListener> drainedListeners = new HashSet<>();
        listenerQueue.drainTo(drainedListeners);
        return drainedListeners;
    }

    protected void notifyListeners(TCPConnectionStatus status, Set<TCPConnectionStatusListener> listenerSet)
    {
        listenerSet.forEach(listener -> {
            if (!inScheduledOutage) {
                switch (status) {
                    case TCPConnection_Success:
                        listener.connectionAttemptSucceed();
                        break;
                    case TCPConnection_Refused:
                        listener.connectionAttemptRefused();
                }
            }
        });
    }

    protected BlockingQueue<TCPConnectionStatusListener> createListenerQueue()
    {
        return new LinkedBlockingQueue<>();
    }

    protected ScheduledExecutorService createSingleThreadScheduleExecutor()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
