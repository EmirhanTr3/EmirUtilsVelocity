package xyz.emirdev.emirutilsvelocity;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class PluginConfig {
    @Configuration
    public static class ServerManager {
        private boolean enabled;
        private String ip;

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getIp() {
            return ip;
        }

        public ServerManager(boolean enabled, String ip) {
            this.enabled = enabled;
            this.ip = ip;
        }

        private ServerManager() {}
    }

    @Configuration
    public static class IPCheck {
        private String token;

        public String getToken() {
            return this.token;
        }

        public IPCheck(String token) {
            this.token = token;
        }

        private IPCheck() {}

    }


    @Comment("Server Manager Configuration")
    private ServerManager serverManager = new ServerManager(false, "127.0.0.1");

    public ServerManager getServerManagerConfig() {
        return this.serverManager;
    }

    @Comment({"IP Check Configuration",
              "- We are using the proxycheck.io API for checking IP data.",
              "- The API is usable with its guest mode, but it's only limted to 100 requests per day.",
              "- By making a free account, you instead get 1000 free requests per day, and if you pay - even more!"})
    private IPCheck ipCheck = new IPCheck("none");

    public IPCheck getIpCheckConfig() {
        return this.ipCheck;
    }
}