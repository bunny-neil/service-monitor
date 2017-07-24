package com.neil.example;

import com.neil.TCPConnectionStatusListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ListenersHelper implements TCPConnectionStatusListener
{
    public static final String GOOGLE_SERVICE = "www.google.com/80";
    public static final String GLOBAL_RELAY = "www.globalrelay.com/80";

    private String service;
    private long frequency;
    private TimeUnit unit;

    public static ListenersHelper createGlobalRelayListener(long frequency)
    {
        return new ListenersHelper(GLOBAL_RELAY, frequency, TimeUnit.SECONDS);
    }

    public static ListenersHelper createGoogleListener(long frequency)
    {
        return new ListenersHelper(GOOGLE_SERVICE, frequency, TimeUnit.SECONDS);
    }

    public ListenersHelper(String service, long frequency, TimeUnit unit)
    {
        this.service = service;
        this.frequency = frequency;
        this.unit = unit;
    }

    @Override
    public void connectionAttemptSucceed()
    {
        System.out.println(service + "-" + frequency + "-" + unit + ": succeeded " + new Date());
    }

    @Override
    public void connectionAttemptRefused()
    {
        System.out.println(service + "-" + frequency + "-" + unit + ": failed" + new Date());
    }

    public String getService() {
        return service;
    }

    public long getFrequency() {
        return frequency;
    }

    public TimeUnit getUnit() {
        return unit;
    }
}
