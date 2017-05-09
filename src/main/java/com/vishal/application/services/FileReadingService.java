package com.vishal.application.services;

import com.vishal.application.entity.LogLine;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.parsers.TraceLogLineParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileReadingService {

    private final TraceLogLineParser logLineParser;

    @Autowired
    public FileReadingService(TraceLogLineParser traceLogLineParser) {
        this.logLineParser = traceLogLineParser;
    }

    public Map<String, List<LogLineInfo>> readFile(String filePath) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            HashMap<String, List<LogLineInfo>> mapOfTraceAndSpans = new HashMap<>();
            List<LogLine> listOfLogLines = stream
                    .map(logLineParser::parse)
                    .collect(Collectors.toList());
            listOfLogLines.sort((LogLine logLine1, LogLine logLine2) -> (logLine1.getLogLineInfo().getEnd().compareTo(logLine2.getLogLineInfo().getEnd())));
            listOfLogLines.forEach(logLineObject -> {
                if (mapOfTraceAndSpans.containsKey(logLineObject.getTrace())) {
                    mapOfTraceAndSpans.get(logLineObject.getTrace()).add(logLineObject.getLogLineInfo());
                } else {
                    List<LogLineInfo> logLineInfoList = new ArrayList<>();
                    logLineInfoList.add(logLineObject.getLogLineInfo());
                    mapOfTraceAndSpans.put(logLineObject.getTrace(), logLineInfoList);
                }
            });
            return mapOfTraceAndSpans;
        } catch (IOException iOException) {
            log.error("IOException occurred", iOException);
            throw iOException;
        }
    }
}
