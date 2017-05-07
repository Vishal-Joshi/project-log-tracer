package com.vishal.application.services;

import com.vishal.application.converters.TraceLogInfoToSpanConverter;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpanOrganisationService {

    private TraceLogInfoToSpanConverter traceLogInfoToSpanConverter;

    @Autowired
    public SpanOrganisationService(TraceLogInfoToSpanConverter traceLogInfoToSpanConverter) {
        this.traceLogInfoToSpanConverter = traceLogInfoToSpanConverter;
    }

    public LogLineInfo fetchRootSpan(List<LogLineInfo> logInfosForTrace) {
        return logInfosForTrace
                .stream()
                .filter(logInfo -> logInfo.getCallerSpan() == null || logInfo.getCallerSpan().equals("null"))
                .collect(Collectors.toList())
                .get(0);
    }

    private LogLineInfo fetchLogLineInfo(String serviceId, List<LogLineInfo> logInfosForTrace) {
        return logInfosForTrace
                .stream()
                .filter(logInfo -> logInfo.getService().equals(serviceId))
                .collect(Collectors.toList())
                .get(0);
    }

    private List<LogLineInfo> fetchChildren(String spanIdOfParent, List<LogLineInfo> logInfosForTrace) {
        return logInfosForTrace
                .stream()
                .filter(logInfo -> logInfo.getCallerSpan().equals(spanIdOfParent))
                .collect(Collectors.toList());
    }

    public List<Span> findRelatedSpans(List<LogLineInfo> logLineInfoList) {
        LogLineInfo rootLogLineInfo = fetchRootSpan(logLineInfoList);
        Span rootSpan = Span
                .builder()
                .service(rootLogLineInfo.getService())
                .start(rootLogLineInfo.getStart())
                .end(rootLogLineInfo.getEnd())
                .calls(logLineInfoList
                        .stream()
                        .filter(logInfo -> logInfo.getCallerSpan().equals(rootLogLineInfo.getSpanId()))
                        .map(traceLogInfoToSpanConverter::convert)
                        .collect(Collectors.toList()))
                .build();

        rootSpan
                .getCalls()
                .forEach(childSpan -> {
                    LogLineInfo currentLogLineInfo = fetchLogLineInfo(childSpan.getService(), logLineInfoList);
                    List<LogLineInfo> currentLogLineChildren = fetchChildren(currentLogLineInfo.getSpanId(), logLineInfoList);
                    childSpan
                            .setCalls(currentLogLineChildren
                                    .stream()
                                    .map(currentChild -> Span.builder()
                                            .service(currentChild.getService())
                                            .start(currentChild.getStart())
                                            .end(currentChild.getEnd())
                                            .calls(logLineInfoList
                                                    .stream()
                                                    .filter(logInfo -> logInfo.getCallerSpan().equals(currentChild.getSpanId()))
                                                    .map(traceLogInfoToSpanConverter::convert)
                                                    .collect(Collectors.toList()))
                                            .build())
                                    .collect(Collectors.toList()));
                });


        return Collections.singletonList(rootSpan);
    }
}
