package com.neil;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.Socket;

import static com.neil.ServiceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TCPConnectionTaskTest
{
    @Test
    public void testRun_WhenSocketIOException_ShouldReturn_TCPConnection_Refused()
    {
        final TCPConnectionTask underTest = new TCPConnectionTask("doesn't matter", 0)
        {
            @Override
            protected Socket createSocket() throws IOException
            {
                throw new IOException();
            }
        };
        assertThat(underTest.run()).isEqualTo(TCPConnection_Refused);
    }

    @Test
    public void testRun_WhenSocketSuccess_ShouldReturn_TCPConnection_Success()
    {
        final TCPConnectionTask underTest = new TCPConnectionTask("doesn't matter", 3306)
        {
            @Override
            protected Socket createSocket() throws IOException
            {
                return mock(Socket.class);
            }
        };
        assertThat(underTest.run()).isEqualTo(TCPConnection_Success);
    }
}
