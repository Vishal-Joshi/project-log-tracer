package com.vishal.application.parsers

import com.vishal.application.entity.LogLine
import org.joda.time.DateTime
import spock.lang.Specification


class TraceLogLineParserTest extends Specification {

    def "should read string to log line objects"(){
        given:
        TraceLogLineParser traceLogLineParser = new TraceLogLineParser()
        String logLine = "2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z eckakaau service7 zfjlsiev->d6m3shqy"

        when:
        LogLine parsedLogLine = traceLogLineParser.parse(logLine)

        then:
        parsedLogLine != null
        "eckakaau" == parsedLogLine.trace
        null != parsedLogLine.span
        new DateTime("2013-10-23T10:12:35.293Z") == parsedLogLine.span.start
        new DateTime("2013-10-23T10:12:35.302Z") == parsedLogLine.span.end
        "zfjlsiev" == parsedLogLine.span.callerSpan
        "d6m3shqy" == parsedLogLine.span.spanId
    }
}
