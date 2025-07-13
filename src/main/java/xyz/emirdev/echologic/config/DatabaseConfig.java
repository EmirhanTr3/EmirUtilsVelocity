package xyz.emirdev.echologic.config;

public class DatabaseConfig {
    private final String address;
    private final int port;
    private final String username;
    private final String password;
    private final String name;

    public DatabaseConfig(String address, int port, String username, String password, String name) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
