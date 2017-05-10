package com.vishal.application;

import com.vishal.application.entity.Span;
import com.vishal.application.entity.Trace;
import org.springframework.stereotype.Component;

/**
 * Created by vishal.joshi on 5/10/17.
 */
@Component
public class TraceObjectFactory {

    public Trace createTraceObject(String traceId, Span rootSpan) {
        return Trace.builder().id(traceId).root(rootSpan).build();
    }

}
