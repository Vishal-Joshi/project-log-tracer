package com.vishal.application.entity;

import lombok.Value;

@Value
public class Trace {
    private String trace;
    private Span root;
}
