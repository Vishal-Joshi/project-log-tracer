package com.vishal.application.entity;

import lombok.Value;

@Value
public class TraceJson {
    private String trace;
    private ServiceSpanJson root;
}
