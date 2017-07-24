package com.neil;

import java.util.concurrent.TimeUnit;

public class TCPConnectionStatusListenerWrapper implements TCPConnectionStatusListener
{
    private TCPConnectionStatusListener listener;
    private long frequency;
    private TimeUnit unit;

    public TCPConnectionStatusListenerWrapper(TCPConnectionStatusListener listener, long frequency, TimeUnit unit)
    {
        this.listener = listener;
        this.frequency = frequency;
        this.unit = unit;
    }

    @Override
    public void connectionAttemptSucceed()
    {
        listener.connectionAttemptSucceed();
    }

    @Override
    public void connectionAttemptRefused()
    {
        listener.connectionAttemptRefused();
    }

    public long getFrequencyInMillis()
    {
        return TimeUnit.MILLISECONDS.convert(getFrequency(), unit);
    }

    public long getFrequency()
    {
        return frequency;
    }

    public TimeUnit getUnit()
    {
        return unit;
    }
}
