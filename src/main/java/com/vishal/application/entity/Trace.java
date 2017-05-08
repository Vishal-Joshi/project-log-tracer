package com.vishal.application.entity;

import lombok.Value;

@Value
public class Trace {
    private String id;
    private Span root;
}
