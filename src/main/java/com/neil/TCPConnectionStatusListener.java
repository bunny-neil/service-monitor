package com.neil;

public interface TCPConnectionStatusListener
{
    void connectionAttemptSucceed();

    void connectionAttemptRefused();
}
