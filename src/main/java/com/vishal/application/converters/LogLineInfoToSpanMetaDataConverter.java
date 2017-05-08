package com.vishal.application.converters;

import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.SpanMetaData;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class LogLineInfoToSpanMetaDataConverter implements Converter<LogLineInfo, SpanMetaData> {

    @Override
    public SpanMetaData convert(LogLineInfo logLineInfo) {
        return SpanMetaData
                .builder()
                .spanId(logLineInfo.getSpanId())
                .span(Span
                        .builder()
                        .service(logLineInfo.getService())
                        .start(logLineInfo.getStart())
                        .end(logLineInfo.getEnd())
                        .calls(new ArrayList<>())
                        .span(logLineInfo.getSpanId())
                        .build())
                .build();
    }
}
