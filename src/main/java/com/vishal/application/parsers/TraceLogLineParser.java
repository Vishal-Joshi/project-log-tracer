package com.vishal.application.parsers;

import com.vishal.application.entity.LogLine;
import com.vishal.application.entity.TraceLogInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class TraceLogLineParser {

    public LogLine parse(String traceLogLineAsString) {
        String[] traceLogLineSplit = traceLogLineAsString.split(" ");
        String[] callerSpanAndOwnSpan = traceLogLineSplit[4].split("->");
        return LogLine
                .builder()
                .trace(traceLogLineSplit[2])
                .traceLogInfo(TraceLogInfo
                        .builder()
                        .start(new DateTime(traceLogLineSplit[0]))
                        .end(new DateTime(traceLogLineSplit[1]))
                        .service(traceLogLineSplit[3])
                        .callerSpan(callerSpanAndOwnSpan[0])
                        .spanId(callerSpanAndOwnSpan[1]).build())
                .build();
    }
}
