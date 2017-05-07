package com.vishal.application.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;

@Getter
@Setter
@Builder
public class Span {
    private String service;
    private DateTime start;
    private DateTime end;
    private List<Span> calls;
}
