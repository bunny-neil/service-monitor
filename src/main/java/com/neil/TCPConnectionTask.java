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

    public TCPConnectionStatus run()
    {
        try(Socket unused = createSocket()) {
            return TCPConnectionStatus.TCPConnection_Success;
        }
        catch (IOException ex) {
            return TCPConnectionStatus.TCPConnection_Refused;
        }
    }

    protected Socket createSocket() throws IOException
    {
        return new Socket(address, port);
    }
}
