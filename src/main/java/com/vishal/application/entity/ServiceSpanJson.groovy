package com.vishal.application.entity

import lombok.Value
import org.joda.time.DateTime

@Value
public class ServiceSpanJson {
    private String service;
    private DateTime start;
    private DateTime end;
    private List<ServiceSpanJson> calls;
}
