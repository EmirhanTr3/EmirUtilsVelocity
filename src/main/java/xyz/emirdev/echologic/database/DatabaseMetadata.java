package xyz.emirdev.echologic.database;

public class DatabaseMetadata {
    private int ping;
    private boolean connected;

    public DatabaseMetadata() {};

    public int ping() {
        return ping;
    }

    public void ping(int ping) {
        this.ping = ping;
    }

    public boolean connected() {
        return connected;
    }

    public void connected(boolean connected) {
        this.connected = connected;
    }

    public String toString() {
        return String.format("DatabaseMetadata{ping=%s,connected=%s}", ping, connected);
    }
}
