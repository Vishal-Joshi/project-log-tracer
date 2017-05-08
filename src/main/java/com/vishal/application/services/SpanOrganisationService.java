package com.vishal.application.services;

import com.vishal.application.ApiConstants;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Span;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vishal.joshi on 5/8/17.
 */
@Service
public class SpanOrganisationService {

    private LogLineInfoOrganisationService logLineInfoOrganisationService;

    public SpanOrganisationService(LogLineInfoOrganisationService logLineInfoOrganisationService) {
        this.logLineInfoOrganisationService = logLineInfoOrganisationService;
    }

    public Span organiseRootSpanAndItsChildren(List<LogLineInfo> logLineInfoList) {
        Map<String, Span> mapOfSpanIdsVsSpan = logLineInfoOrganisationService.buildMapOfSpanIdsVsSpan(logLineInfoList);
        //convert list of log line info to list of spans for each caller span id
        Map<String, List<Span>> mapOfCallerIdsVsSpan = logLineInfoOrganisationService
                .buildMapOfCallerSpanIdsVsLogLineInfo(logLineInfoList)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entrySetValues -> entrySetValues
                        .getValue()
                        .stream()
                        .map(logLineInfo -> mapOfSpanIdsVsSpan.get(logLineInfo.getSpanId()))
                        .collect(Collectors.toList())));

        //attach children
        mapOfCallerIdsVsSpan.entrySet()
                .forEach(entrySet -> mapOfSpanIdsVsSpan
                        .get(entrySet.getKey())
                        .setCalls(entrySet.getValue()));
        return mapOfCallerIdsVsSpan.get(ApiConstants.TRACE_INITIATOR_SPAN_ID).get(0);
    }
}
