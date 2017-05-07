package com.vishal.application.services

import com.vishal.application.converters.TraceLogInfoToSpanConverter
import com.vishal.application.entity.Span
import com.vishal.application.entity.TraceLogInfo
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Specification

class SpanOrganisationServiceTest extends Specification {

    TraceLogInfoToSpanConverter mockTraceLogInfoToSpanConverter = Mockito.mock(TraceLogInfoToSpanConverter.class)

    def "should be able to find out root span"() {
        given:
        SpanOrganisationService spanOrganisationService = new SpanOrganisationService(mockTraceLogInfoToSpanConverter)

        def rootSpan = TraceLogInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def backEnd2Span = TraceLogInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-2")
                .callerSpan("aa")
                .spanId("ab")
                .build()

        def backEnd1Span = TraceLogInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-1")
                .callerSpan("aa")
                .spanId("ac")
                .build()

        def backEnd3Span = TraceLogInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-3")
                .callerSpan("ac")
                .spanId("ad")
                .build()

        def trace1 = "trace1"
        def traceLogInfos = [rootSpan, backEnd1Span, backEnd2Span, backEnd3Span]

        Mockito.when(mockTraceLogInfoToSpanConverter.convert(rootSpan)).thenReturn(new Span(rootSpan.service, rootSpan.start, rootSpan.end, null))

        when:
        Span actualRootSpan = spanOrganisationService.fetchRootSpan(trace1, traceLogInfos)

        then:
        null != actualRootSpan
        rootSpan.service.equals("front-end")
    }
}
