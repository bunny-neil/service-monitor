package com.neil;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TCPConnectionMonitorTest
{
    private TCPConnectionMonitor underTest;
    private BlockingQueue<TCPConnectionStatusListener> mockListenersQueue;
    private ScheduledExecutorService mockExecutor;

    @Before
    public void setup()
    {
        mockListenersQueue = mock(BlockingQueue.class);
        mockExecutor = mock(ScheduledExecutorService.class);
        underTest = new TCPConnectionMonitor("doesn't matter", 0)
        {
            @Override
            protected ScheduledExecutorService createSingleThreadScheduleExecutor()
            {
                return mockExecutor;
            }

            @Override
            protected BlockingQueue<TCPConnectionStatusListener> createListenerQueue()
            {
                return mockListenersQueue;
            }
        };
    }

    @Test
    public void testRun_GivenEmptyQueue_ShouldNotRunTask()
    {

    }

    @Test
    public void testStart_GivenNothing_ShouldScheduleWithFixedDelayGetCalledOnce()
    {
        underTest.start();
        verify(mockExecutor, times(1)).scheduleWithFixedDelay(
                underTest, 0L, 1L, TimeUnit.SECONDS);
    }

    @Test
    public void testShutdown_GivenNothing_ShouldShutdownGetCalledTwice()
    {
        underTest.shutdown();
        verify(mockExecutor, times(2)).shutdown();
    }

    @Test
    public void testRegisterListener_GivenEmptyQueueAddNewListenerShouldHaveOneistener()
    {
        TCPConnectionStatusListener mockListener = mock(TCPConnectionStatusListener.class);
        underTest.registerListener(mockListener);
        verify(mockListenersQueue, times(1)).add(mockListener);
    }
}
