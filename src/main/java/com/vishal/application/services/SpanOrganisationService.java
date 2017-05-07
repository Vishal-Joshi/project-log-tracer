package com.vishal.application.services;

import com.vishal.application.converters.LogLineInfoToSpanMetaDataConverter;
import com.vishal.application.converters.TraceLogInfoToSpanConverter;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.SpanMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpanOrganisationService {

    private TraceLogInfoToSpanConverter traceLogInfoToSpanConverter;
    private LogLineInfoOrganisationService logLineInfoOrganisationService;
    private LogLineInfoToSpanMetaDataConverter logLineInfoToSpanMetaDataConverter;

    @Autowired
    public SpanOrganisationService(TraceLogInfoToSpanConverter traceLogInfoToSpanConverter,
                                   LogLineInfoOrganisationService logLineInfoOrganisationService,
                                   LogLineInfoToSpanMetaDataConverter logLineInfoToSpanMetaDataConverter) {
        this.traceLogInfoToSpanConverter = traceLogInfoToSpanConverter;
        this.logLineInfoOrganisationService = logLineInfoOrganisationService;
        this.logLineInfoToSpanMetaDataConverter = logLineInfoToSpanMetaDataConverter;
    }

    public List<Span> findRelatedSpans(List<LogLineInfo> logLineInfoList) {
        Map<String, List<LogLineInfo>> mapOfCallerSpanAndLogLineInfo =
                logLineInfoOrganisationService.buildMapOfLogLineRelatedByCallerSpan(logLineInfoList);
        //convert of all keys which are caller span ids to span metadata objects.
        List<SpanMetaData> spansMetaDataForEachCallerSpanIds = mapOfCallerSpanAndLogLineInfo
                .keySet()
                .stream()
                .map(callerSpanId -> logLineInfoList
                        .stream()
                        .filter(currentLogLineInfo -> currentLogLineInfo.getSpanId().equals(callerSpanId))
                        .findFirst())
                .filter(Optional::isPresent)
                .map((logLineInfoOptional) -> logLineInfoToSpanMetaDataConverter.convert(logLineInfoOptional.get()))
                .collect(Collectors.toList());

        //set calls attribute of each spanMetaData.span to the values of mapOfCallerSpanAndLogLineInfo
        spansMetaDataForEachCallerSpanIds
                .forEach(spanMetaData -> spanMetaData
                        .getSpan()
                        .setCalls(mapOfCallerSpanAndLogLineInfo
                                .get(spanMetaData.getSpanId())
                                .stream()
                                .map(logLineInfo -> logLineInfoToSpanMetaDataConverter.convert(logLineInfo).getSpan())
                                .collect(Collectors.toList())));

        //link span objects of keys to span objects in values of map
        List<String> rootSpanIds = mapOfCallerSpanAndLogLineInfo.get("null")
                .stream()
                .map(LogLineInfo::getSpanId)
                .collect(Collectors.toList());

        //fetch all spans matching root span ids and put them to final list of spans
        return rootSpanIds
                .stream()
                .map(spanId -> spansMetaDataForEachCallerSpanIds
                        .stream()
                        .filter(spanMetaData -> spanMetaData.getSpanId().equals(spanId))
                        .findFirst())
                .filter(Optional::isPresent)
                .map((optionalSpanMetaData) -> optionalSpanMetaData.get().getSpan())
                .collect(Collectors.toList());

    }

}
