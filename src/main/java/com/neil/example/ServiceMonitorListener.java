package com.neil.example;

import com.neil.TCPConnectionStatusListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ServiceMonitorListener implements TCPConnectionStatusListener
{
    private String service;
    private long frequency;
    private TimeUnit unit;

    public ServiceMonitorListener(String service, long frequency, TimeUnit unit)
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
