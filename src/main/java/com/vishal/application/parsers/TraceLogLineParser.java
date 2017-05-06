package com.vishal.application.parsers;

import com.vishal.application.entity.LogLine;
import com.vishal.application.entity.Span;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class TraceLogLineParser {

    public LogLine parse(String traceLogLineAsString){
        String[] traceLogLineSplit = traceLogLineAsString.split(" ");
        String[] callerSpanAndOwnSpan = traceLogLineSplit[4].split("->");
        return new LogLine(traceLogLineSplit[2],
                new Span(new DateTime(traceLogLineSplit[0]),
                        new DateTime(traceLogLineSplit[1]),
                        traceLogLineSplit[3],
                        callerSpanAndOwnSpan[0],
                        callerSpanAndOwnSpan[1]));
    }
}
