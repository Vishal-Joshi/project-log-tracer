package com.vishal.application.controllers;

import com.vishal.application.entity.Span;
import com.vishal.application.entity.Trace;
import com.vishal.application.services.FileReadingService;
import com.vishal.application.services.SpanOrganisationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class LogReadController {

    private final FileReadingService fileReadingService;
    private final SpanOrganisationService spanOrganisationService;

    @Autowired
    public LogReadController(FileReadingService fileReadingService, SpanOrganisationService spanOrganisationService) {

        this.fileReadingService = fileReadingService;
        this.spanOrganisationService = spanOrganisationService;
    }

    @GetMapping(path = "/readlogs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Trace> readLogs() {
        ClassLoader classLoader = getClass().getClassLoader();
        List<Trace> traces = new ArrayList<>();
        try {
            fileReadingService.readFile(classLoader.getResource("logs/small-log.txt").getFile()).entrySet().forEach(set -> {

                List<Span> roots = spanOrganisationService.findRelatedSpans(set.getValue());
                if (roots.size() > 1) {
                    log.warn("More than 1 roots available for trace:{}", set.getKey());
                }
                if (roots.isEmpty()) {
                    log.warn("No roots available for trace:{}", set.getKey());
                } else {
                    traces.add(new Trace(set.getKey(), roots.get(0)));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return traces;
    }
}
