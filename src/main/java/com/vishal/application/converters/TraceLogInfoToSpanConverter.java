package com.vishal.application.converters;

import com.vishal.application.entity.Span;
import com.vishal.application.entity.LogLineInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TraceLogInfoToSpanConverter implements Converter<LogLineInfo, Span> {

    @Override
    public Span convert(LogLineInfo logLineInfo) {
        return Span
                .builder()
                .service(logLineInfo.getService())
                .start(logLineInfo.getStart())
                .end(logLineInfo.getEnd())
                .build();
    }
}
