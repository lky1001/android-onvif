package com.tistory.lky1001.androidonvif.lib.model;

public class OnvifDevice {

    private String host;
    private String username;
    private String password;
    private boolean connected;

    public OnvifDevice(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        if (host.startsWith("http://") || host.startsWith("https://")) {
            return host;
        }

        return "http://" + host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
