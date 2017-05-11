package com.vishal.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.application.entity.LogLine;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Trace;
import com.vishal.application.parsers.TraceLogLineParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vishal.joshi on 5/11/17.
 */
@Component
public class IncrementalFileReadingService {

    @Autowired
    private TraceLogLineParser traceLogLineParser;

    private Map<String, List<LogLineInfo>> mapOfTraceIdsVsLogLineInfo;

    @Autowired
    private SpanOrganisationService spanOrganisationService;

    @Autowired
    private ConsoleOutputService consoleOutputService;

    @Autowired
    private FileOutputService fileOutputService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${base.path}")
    private String inputLogFileBasePath;

    public void readFileLineByLine(String inputLogFileName, String outputLogFileName) {
        mapOfTraceIdsVsLogLineInfo = new HashMap<>();
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(new File(inputLogFileBasePath + inputLogFileName), "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    LogLine logLine = traceLogLineParser.parse(line);
                    List<LogLineInfo> logLineInfos = mapOfTraceIdsVsLogLineInfo.get(logLine.getTrace());
                    if (logLineInfos != null) {
                        logLineInfos.add(logLine.getLogLineInfo());
                        logLineInfos.sort(Comparator.comparing(LogLineInfo::getEnd));

                        LogLineInfo rootLogLineInfo = getRootLogLineInfo(logLineInfos);
                        if (rootLogLineInfo != null) {
                            Trace trace = Trace
                                    .builder()
                                    .id(logLine.getTrace())
                                    .root(spanOrganisationService.organiseRootSpanAndItsChildren(logLineInfos))
                                    .build();

                            String traceJsonAsString = objectMapper.writeValueAsString(trace);
                            if (it.hasNext()) {
                                traceJsonAsString += "\r\n";
                            }
                            consoleOutputService.printJson(traceJsonAsString);
                            fileOutputService.printJson(traceJsonAsString, outputLogFileName);
                        }
//                        else {
//                            logLineInfos.sort(Comparator.comparing(LogLineInfo::getStart));
//
//                            if (logLineInfos.get(0).getStart().plusSeconds(30).isBefore(DateTime.now())) {
//                                // abandon these log lines as orphans
//                                System.err.println("Log lines with trace id: " + logLine.getTrace() + " is being orphaned!");
//                                mapOfTraceIdsVsLogLineInfo.remove(logLine.getTrace());
//                            }
//                        }
                    } else {
                        logLineInfos = new ArrayList<>();
                    }
                    mapOfTraceIdsVsLogLineInfo.put(logLine.getTrace(), logLineInfos);
                    // do something with line
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LogLineInfo getRootLogLineInfo(List<LogLineInfo> logLineInfoList) {
        List<LogLineInfo> rootLogLineInfoList = logLineInfoList
                .stream()
                .filter(logLineInfo -> logLineInfo.getCallerSpan().equals("null"))
                .collect(Collectors.toList());
        return !rootLogLineInfoList.isEmpty() ? rootLogLineInfoList.get(0) : null;
    }
}
