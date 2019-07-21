package com.tistory.lky1001.androidonvif.lib.model;

import org.simpleframework.xml.Element;

public class GetDeviceInformationBody extends RequestBody {

    @Element(name = "GetDeviceInformation")
    private GetDeviceInformationRequest request;
}
