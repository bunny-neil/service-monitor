package com.neil;

import java.io.IOException;
import java.net.Socket;

class TCPConnectionTask
{
    private String address;
    private int port;

    public TCPConnectionTask(String address, int port)
    {
        this.address = address;
        this.port = port;
    }

    public ServiceStatus run()
    {
        try(Socket unused = createSocket()) {
            return ServiceStatus.TCPConnection_Success;
        }
        catch (IOException ex) {
            return ServiceStatus.TCPConnection_Refused;
        }
    }

    protected Socket createSocket() throws IOException
    {
        return new Socket(address, port);
    }
}
