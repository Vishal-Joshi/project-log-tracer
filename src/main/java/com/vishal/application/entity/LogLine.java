package com.vishal.application.entity;

import lombok.Value;

@Value
public class LogLine {
    private String trace;
    private Span span;
}
