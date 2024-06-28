package xyz.emirdev.emirutilsvelocity;

import java.util.Map;

public class IPData {
    private final boolean isUsingKey;
    private final String status;
    private final String message;
    private final String ip;
    private final String provider;
    private final String organisation;
    private final String city;
    private final String region;
    private final String country;
    private final double latitude;
    private final double longitude;
    private final String type;
    private final boolean isVPN;
    private final boolean isProxy;
    private final double risk;
    private final String riskName;

    public IPData(Map<String, Object> map) {
        this.isUsingKey = (boolean) map.get("isUsingKey");
        this.status = (String) map.get("status");
        this.message = (String) map.get("message");
        this.ip = (String) map.get("ip");
        this.provider = (String) map.get("provider");
        this.organisation = (String) map.get("organisation");
        this.city = (String) map.get("city");
        this.region = (String) map.get("region");
        this.country = (String) map.get("country");
        this.latitude = (double) map.getOrDefault("latitude", 0D);
        this.longitude = (double) map.getOrDefault("longitude", 0D);
        this.type = (String) map.get("type");
        this.isVPN = map.getOrDefault("vpn", "no").equals("yes");
        this.isProxy = map.getOrDefault("proxy", "no").equals("yes");
        this.risk = (double) map.getOrDefault("risk", -1D);
        this.riskName = this.risk >= 66 ? "Very Risky" : this.risk >= 33 ? "Risky" : "Safe";
    }

    public boolean isUsingKey() {
        return this.isUsingKey;
    }

    public String getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public String getIp() {
        return this.ip;
    }

    public String getProvider() {
        return this.provider;
    }

    public String getOrganisation() {
        return this.organisation;
    }

    public String getLocation() {
        return String.format("%s, %s, %s (%s, %s)", this.city, this.region, this.country, latitude, longitude);
    }

    public String getType() {
        return this.type;
    }

    public boolean isVPN() {
        return this.isVPN;
    }

    public boolean isProxy() {
        return this.isProxy;
    }

    public double getRisk() {
        return this.risk;
    }

    public String getRiskName() {
        return this.riskName;
    }
}
