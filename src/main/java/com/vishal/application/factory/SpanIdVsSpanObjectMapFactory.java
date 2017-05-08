package com.vishal.application.factory;

import com.vishal.application.ApiConstants;
import com.vishal.application.entity.Span;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal.joshi on 5/8/17.
 */
@Component
public class SpanIdVsSpanObjectMapFactory {

    public Map<String, Span> create() {
        HashMap<String, Span> mapOfSpanIdVsSpan = new HashMap<>();
        mapOfSpanIdVsSpan.put(ApiConstants.TRACE_INITIATOR_SPAN_ID, Span.builder().build());
        return mapOfSpanIdVsSpan;
    }
}
