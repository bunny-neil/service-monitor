package com.neil;

import java.io.IOException;
import java.net.Socket;

class TCPConnectionTask
{
    private String address;
    private int port;
    private long delay;

    public TCPConnectionTask(String address, int port)
    {
        this.address = address;
        this.port = port;
    }

    public ServiceStatus run()
    {
        try(Socket unused = new Socket(address, port)) {
            return ServiceStatus.TCPConnection_Success;
        }
        catch (IOException ex) {
            return ServiceStatus.TCPConnection_Refused;
        }
    }
}
