package com.vishal.application.services;

import com.vishal.application.TraceObjectFactory;
import com.vishal.application.entity.Span;
import com.vishal.application.entity.Trace;
import com.vishal.application.exception.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishal.joshi on 5/10/17.
 */
@Service
@Slf4j
public class LogReadingService {

    private FileReadingService fileReadingService;
    private SpanOrganisationService spanOrganisationService;
    private TraceOrderingService traceOrderingService;
    private TraceObjectFactory traceObjectFactory;

    @Autowired
    public LogReadingService(FileReadingService fileReadingService,
                             SpanOrganisationService spanOrganisationService,
                             TraceOrderingService traceOrderingService,
                             TraceObjectFactory traceObjectFactory) {
        this.fileReadingService = fileReadingService;
        this.spanOrganisationService = spanOrganisationService;
        this.traceOrderingService = traceOrderingService;
        this.traceObjectFactory = traceObjectFactory;
    }

    public List<Trace> buildTraceAndSpan(String logFileName) {
        try {
            List<Trace> resultantTraceList = new ArrayList<>();
            fileReadingService.readFile(logFileName).entrySet()
                    .forEach(traceIdAndLogLineInfoListEntrySet -> {
                        Span root = spanOrganisationService.organiseRootSpanAndItsChildren(traceIdAndLogLineInfoListEntrySet.getValue());
                        resultantTraceList.add(traceObjectFactory.createTraceObject(traceIdAndLogLineInfoListEntrySet.getKey(), root));
                    });

            return traceOrderingService.orderByStartDateOfRootSpan(resultantTraceList);
        } catch (IOException ioException) {
            log.error("Exception while reading log file with name:{}", logFileName, ioException);
            throw new InternalServerError(String.format("Exception while reading log file with name:%s", logFileName), ioException);
        }
    }
}
