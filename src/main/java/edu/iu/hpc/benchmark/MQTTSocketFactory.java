package edu.iu.hpc.benchmark;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class MQTTSocketFactory extends SocketFactory {

    public Socket createSocket() throws SocketException {
        Socket socket = new Socket();
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        return socket;
    }

    public Socket createSocket(String var1, int var2) throws IOException {
        Socket socket = new Socket(var1, var2);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        return socket;
    }

    public Socket createSocket(InetAddress var1, int var2) throws IOException {
        Socket socket = new Socket(var1, var2);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        return socket;
    }

    public Socket createSocket(String var1, int var2, InetAddress var3, int var4) throws IOException {
        Socket socket = new Socket(var1, var2, var3, var4);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        return socket;
    }

    public Socket createSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException {
        Socket socket = new Socket(var1, var2, var3, var4);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        return socket;
    }
}
