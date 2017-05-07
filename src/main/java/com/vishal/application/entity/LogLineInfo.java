package com.vishal.application.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@Builder
public class LogLineInfo {
    private DateTime start;
    private DateTime end;
    private String service;
    private String callerSpan;
    private String spanId;
}
