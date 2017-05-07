package com.vishal.application.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SpanMetaData {
    private Span span;
    private String spanId;
}
