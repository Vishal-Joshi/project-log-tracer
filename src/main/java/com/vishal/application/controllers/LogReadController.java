package com.vishal.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.Trace;
import com.vishal.application.services.FileReadingService;
import com.vishal.application.services.SpanOrganisationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class LogReadController {

    private final FileReadingService fileReadingService;
    private final SpanOrganisationService spanOrganisationService;
    private ObjectMapper objectMapper;

    @Autowired
    public LogReadController(FileReadingService fileReadingService,
                             SpanOrganisationService spanOrganisationService,
                             ObjectMapper objectMapper) {
        this.fileReadingService = fileReadingService;
        this.spanOrganisationService = spanOrganisationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/readlogs", produces = MediaType.TEXT_PLAIN_VALUE)
    public String readLogs() {
        ClassLoader classLoader = getClass().getClassLoader();
        StringBuilder resultantJsonString = new StringBuilder();
        try {
            fileReadingService.readFile(classLoader.getResource("logs/small-log.txt").getFile())
                    .entrySet()
                    .forEach(set -> {
                        Span root = spanOrganisationService.organiseRootSpanAndItsChildren(set.getValue());
                        Trace trace = new Trace(set.getKey(), root);
                        try {
                            resultantJsonString.append(objectMapper.writeValueAsString(trace));
                        } catch (JsonProcessingException jsonProcessingException) {
                            log.error("exception occurred while json serialisation of trace:", trace.toString(), jsonProcessingException);
                        }
                    });
        } catch (IOException ioException) {
            log.error("exception occurred", ioException);
        }
        return resultantJsonString.toString();
    }
}
