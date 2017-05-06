package com.vishal.application.entity;

import lombok.Value;
import org.joda.time.DateTime;

@Value
public class Span {
    private DateTime start;
    private DateTime end;
    private String service;
    private String callerSpan;
    private String span;
}
