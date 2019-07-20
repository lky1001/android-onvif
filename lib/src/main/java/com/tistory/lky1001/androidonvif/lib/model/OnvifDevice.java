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
}
