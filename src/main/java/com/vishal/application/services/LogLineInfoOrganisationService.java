package com.vishal.application.services;

import com.vishal.application.entity.LogLineInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LogLineInfoOrganisationService {

    public Map<String, List<LogLineInfo>> buildMapOfLogLineRelatedByCallerSpan(List<LogLineInfo> logLineInfos) {
        return logLineInfos.stream().collect(Collectors.groupingBy(LogLineInfo::getCallerSpan));
    }

}

