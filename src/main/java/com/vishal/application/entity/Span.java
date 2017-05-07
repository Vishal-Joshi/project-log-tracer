package com.vishal.application.entity;

import lombok.Value;
import org.joda.time.DateTime;

import java.util.List;

@Value
public class Span {
    private String service;
    private DateTime start;
    private DateTime end;
    private List<Span> calls;
}
