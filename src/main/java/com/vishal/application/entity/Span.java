package com.vishal.application.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private DateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private DateTime end;
    private List<Span> calls;
}
