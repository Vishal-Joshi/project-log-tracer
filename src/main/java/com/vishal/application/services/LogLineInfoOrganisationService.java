package com.vishal.application.services;

import com.vishal.application.ApiConstants;
import com.vishal.application.converters.LogLineInfoToSpanMetaDataConverter;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.SpanMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class LogLineInfoOrganisationService {

    private LogLineInfoToSpanMetaDataConverter logLineInfoToSpanMetaDataConverter;

    @Autowired
    public LogLineInfoOrganisationService(LogLineInfoToSpanMetaDataConverter logLineInfoToSpanMetaDataConverter) {
        this.logLineInfoToSpanMetaDataConverter = logLineInfoToSpanMetaDataConverter;
    }

    public Map<String, List<LogLineInfo>> buildMapOfCallerSpanIdsVsLogLineInfo(List<LogLineInfo> logLineInfos) {
        return logLineInfos
                .stream()
                .collect(Collectors.groupingBy(LogLineInfo::getCallerSpan,
                        toSortedList(Comparator.comparing(LogLineInfo::getStart))));
    }

    private static <T> Collector<T, ?, List<T>> toSortedList(Comparator<? super T> startDateComparator) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new), list -> {
                    list.sort(startDateComparator);
                    return list;
                });
    }

    public Map<String, Span> buildMapOfSpanIdsVsSpan(List<LogLineInfo> logLineInfos) {
        Map<String, Span> mapOfSpanIdsVsSpan = logLineInfos
                .stream()
                .map(logLineInfo -> logLineInfoToSpanMetaDataConverter.convert(logLineInfo))
                .collect(Collectors.toMap(SpanMetaData::getSpanId, SpanMetaData::getSpan));
        mapOfSpanIdsVsSpan.put(ApiConstants.TRACE_INITIATOR_SPAN_ID, Span.builder().build());
        return mapOfSpanIdsVsSpan;
    }

    public List<String> orderTraceIdsByEarliestFinishingSpan(Map<String, List<LogLineInfo>> mapOfTraceIdsVsLogLineInfo) {
        return mapOfTraceIdsVsLogLineInfo
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entrySet -> entrySet.getValue().get(0).getEnd(), Map.Entry::getKey, (traceId1, traceId2) -> {
                    System.out.println("duplicate key found! "+ traceId1+" -- "+traceId2);
                    return traceId1;
                }))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

}

