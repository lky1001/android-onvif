package com.tistory.lky1001.androidonvif.lib.model;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "GetDeviceInformation", strict = false)
@Namespace(reference = "http://www.onvif.org/ver10/device/wsdl")
public class GetDeviceInformationRequest {
}
