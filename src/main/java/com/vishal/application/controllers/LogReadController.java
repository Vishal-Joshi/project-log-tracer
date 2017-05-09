package com.vishal.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.application.entity.LogLineInfo;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.Trace;
import com.vishal.application.services.FileReadingService;
import com.vishal.application.services.SpanOrganisationService;
import com.vishal.application.services.TraceOrderingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class LogReadController {

    private final FileReadingService fileReadingService;
    private final SpanOrganisationService spanOrganisationService;
    private ObjectMapper objectMapper;
    private TraceOrderingService traceOrderingService;

    @Autowired
    public LogReadController(FileReadingService fileReadingService,
                             SpanOrganisationService spanOrganisationService,
                             ObjectMapper objectMapper,
                             TraceOrderingService traceOrderingService) {
        this.fileReadingService = fileReadingService;
        this.spanOrganisationService = spanOrganisationService;
        this.objectMapper = objectMapper;
        this.traceOrderingService = traceOrderingService;
    }

    @GetMapping(path = "/readlogs", produces = MediaType.TEXT_PLAIN_VALUE)
    public String readLogs() {
        ClassLoader classLoader = getClass().getClassLoader();
        StringBuilder resultantJsonString = new StringBuilder();
        List<Trace> resultantTraceList = new ArrayList<>();
        try {
            Map<String, List<LogLineInfo>> traceIdVsLogLineInfo = fileReadingService.readFile(classLoader.getResource("logs/small-log.txt").getFile());
            traceIdVsLogLineInfo
                    .entrySet()
                    .forEach(set -> {
                        Span root = spanOrganisationService.organiseRootSpanAndItsChildren(set.getValue());
                        Trace trace = Trace.builder().id(set.getKey()).root(root).build();
                        resultantTraceList.add(trace);
                    });

            traceOrderingService
                    .orderByEarliestFinishingSpan(resultantTraceList, traceIdVsLogLineInfo)
                    .forEach(trace -> {
                        try {
                            resultantJsonString.append(objectMapper.writeValueAsString(trace) + "\r\n ");
                        } catch (JsonProcessingException jsonProcessingException) {
                            log.error("exception occurred while json serialisation of trace:", trace.toString(), jsonProcessingException);
                        }
                    });

        } catch (IOException ioException) {
            log.error("exception occurred while reading log file", ioException);
        }
        return resultantJsonString.toString();
    }
}
