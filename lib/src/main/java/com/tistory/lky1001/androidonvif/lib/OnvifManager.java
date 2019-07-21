package com.tistory.lky1001.androidonvif.lib;

import android.support.annotation.NonNull;

import com.burgstaller.okhttp.digest.Credentials;
import com.tistory.lky1001.androidonvif.lib.model.OnvifDevice;

public class OnvifManager {

    public OnvifManager(@NonNull String host, @NonNull String username, @NonNull String password) {
        ServiceBuilder.setCredentials(new Credentials(username, password));
    }

    public void discovery() {

    }

    public void getDevices() {

    }

    public void getStreamUri(OnvifDevice device) {

    }
}
