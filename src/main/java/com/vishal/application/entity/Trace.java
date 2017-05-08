package com.vishal.application.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Trace {
    private String id;
    private Span root;
}
