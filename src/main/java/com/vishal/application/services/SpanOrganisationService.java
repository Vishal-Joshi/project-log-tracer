package com.vishal.application.services;

import com.vishal.application.converters.TraceLogInfoToSpanConverter;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.TraceLogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpanOrganisationService {

    private TraceLogInfoToSpanConverter traceLogInfoToSpanConverter;

    @Autowired
    public SpanOrganisationService(TraceLogInfoToSpanConverter traceLogInfoToSpanConverter) {
        this.traceLogInfoToSpanConverter = traceLogInfoToSpanConverter;
    }

    public Span fetchRootSpan(String traceId, List<TraceLogInfo> logInfosForTrace) {
        return traceLogInfoToSpanConverter.convert(logInfosForTrace
                .stream()
                .filter(logInfo -> logInfo.getCallerSpan() == null || logInfo.getCallerSpan().equals("null"))
                .collect(Collectors.toList()).get(0));
    }
}
