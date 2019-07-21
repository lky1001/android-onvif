package com.tistory.lky1001.androidonvif.lib.service;

import com.tistory.lky1001.androidonvif.lib.model.RequestEnvelope;
import com.tistory.lky1001.androidonvif.lib.model.ResponseEnvelope;

import io.reactivex.Single;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DeviceInformationService {

    @Headers({ "Content-Type: application/soap+xml; charset=utf-8;"})
    @POST("onvif/device_service")
    Single<ResponseEnvelope> getDeviceInformation(RequestEnvelope request);
}
