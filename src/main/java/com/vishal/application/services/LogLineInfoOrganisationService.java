package com.vishal.application.services;

import com.vishal.application.converters.LogLineInfoToSpanMetaDataConverter;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.SpanMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
                .collect(Collectors.groupingBy(LogLineInfo::getCallerSpan));
    }

    public Map<String, Span> buildMapOfSpanIdsVsSpan(List<LogLineInfo> logLineInfos) {
        return logLineInfos
                .stream()
                .map(logLineInfo -> logLineInfoToSpanMetaDataConverter.convert(logLineInfo))
                .collect(Collectors.toMap(SpanMetaData::getSpanId, SpanMetaData::getSpan));
    }

}

