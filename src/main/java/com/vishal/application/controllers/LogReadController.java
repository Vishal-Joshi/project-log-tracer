package com.vishal.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.application.exception.InternalServerError;
import com.vishal.application.printer.Printer;
import com.vishal.application.services.LogReadingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LogReadController {

    private String basePath;
    private LogReadingService logReadingService;
    private ObjectMapper objectMapper;
    private Printer printer;

    @Autowired
    public LogReadController(@Value("${base.path}") String basePath,
                             LogReadingService logReadingService,
                             ObjectMapper objectMapper,
                             Printer printer) {
        this.basePath = basePath;
        this.logReadingService = logReadingService;
        this.objectMapper = objectMapper;
        this.printer = printer;
    }

    @GetMapping(path = "/readlogs", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> readLogs(@RequestParam(value = "traceinputlogfilename", required = true) String fileName,
                                           @RequestParam(value = "tracesoutputfilename", required = true) String outputFileName) {
        String resultantJson = logReadingService.buildTraceAndSpan(basePath + fileName)
                .stream()
                .map(trace -> {
                    try {
                        return objectMapper.writeValueAsString(trace) + "\r\n";
                    } catch (JsonProcessingException jsonProcessingException) {
                        log.error("Error occurred while making trace json: {}", trace.toString(), jsonProcessingException);
                        throw new InternalServerError("Error occurred while making trace json", jsonProcessingException);
                    }
                })
                .reduce("", String::concat)
                .trim();
        printer.print(resultantJson, outputFileName);
        return ResponseEntity.ok("successful");
    }
}
