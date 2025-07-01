package xyz.emirdev.emirutilsvelocity.parameters;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class PlayerIPAddress extends InetSocketAddress {
    public PlayerIPAddress(int port) {
        super(port);
    }

    public PlayerIPAddress(InetAddress addr, int port) {
        super(addr, port);
    }

    public PlayerIPAddress(String hostname, int port) {
        super(hostname, port);
    }

}
