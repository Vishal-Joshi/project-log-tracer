package com.vishal.application.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LogLine {
    private String trace;
    private TraceLogInfo traceLogInfo;

}
