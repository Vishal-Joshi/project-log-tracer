package com.vishal.application.converters;

import com.vishal.application.entity.Span;
import com.vishal.application.entity.TraceLogInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TraceLogInfoToSpanConverter implements Converter<TraceLogInfo, Span> {

    @Override
    public Span convert(TraceLogInfo traceLogInfo) {
        return new Span(traceLogInfo.getService(), traceLogInfo.getStart(), traceLogInfo.getEnd(), null);
    }
}
