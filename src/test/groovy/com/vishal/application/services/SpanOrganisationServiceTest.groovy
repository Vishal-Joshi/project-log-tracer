package com.vishal.application.services

import com.vishal.application.converters.LogLineInfoToSpanMetaDataConverter
import com.vishal.application.converters.TraceLogInfoToSpanConverter
import com.vishal.application.entity.LogLineInfo
import com.vishal.application.entity.Span
import com.vishal.application.entity.SpanMetaData
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Specification

class SpanOrganisationServiceTest extends Specification {

    TraceLogInfoToSpanConverter mockTraceLogInfoToSpanConverter = Mockito.mock(TraceLogInfoToSpanConverter.class)

    LogLineInfoToSpanMetaDataConverter mockLogLineInfoToSpanMetaDataConverter = Mockito.mock(LogLineInfoToSpanMetaDataConverter.class)

    LogLineInfoOrganisationService mockLogLineInfoOrganisationService = Mockito.mock(LogLineInfoOrganisationService.class)

    def "should be able to set spans/service calls called from current span/service in 'calls' attribute of span object"() {
        given:
        SpanOrganisationService spanOrganisationService = new SpanOrganisationService(mockTraceLogInfoToSpanConverter, mockLogLineInfoOrganisationService, mockLogLineInfoToSpanMetaDataConverter)

        def rootSpan = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def backEnd2Span = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-2")
                .callerSpan("aa")
                .spanId("ab")
                .build()

        def backEnd1Span = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-1")
                .callerSpan("aa")
                .spanId("ac")
                .build()

        def backEnd3Span = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-3")
                .callerSpan("ac")
                .spanId("ad")
                .build()

        def traceLogInfos = [rootSpan, backEnd1Span, backEnd2Span, backEnd3Span]

        Mockito.when(mockLogLineInfoOrganisationService.buildMapOfLogLineRelatedByCallerSpan(traceLogInfos))
                .thenReturn(["null": [rootSpan], "aa": [backEnd2Span, backEnd1Span], "ac": [backEnd3Span]])

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(rootSpan))
                .thenReturn(new SpanMetaData(new Span(rootSpan.service, rootSpan.start, rootSpan.end, null), "aa"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd1Span))
                .thenReturn(new SpanMetaData(new Span(backEnd1Span.service, backEnd1Span.start, backEnd1Span.end, null), "ac"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd2Span))
                .thenReturn(new SpanMetaData(new Span(backEnd2Span.service, backEnd2Span.start, backEnd2Span.end, null), "ab"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd3Span))
                .thenReturn(new SpanMetaData(new Span(backEnd3Span.service, backEnd3Span.start, backEnd3Span.end, null), "ad"))

        when:
        List<Span> relatedSpans = spanOrganisationService.findRelatedSpans(traceLogInfos)

        then:
        null != relatedSpans
        1 == relatedSpans.size()
        def actualRootSpan = relatedSpans.get(0)
        "front-end" == actualRootSpan.service
        def actualBackEnd1Span = actualRootSpan.calls.find { it.service.equals("back-end-1") }
        null != actualBackEnd1Span
        null != actualBackEnd1Span.calls
        1 == actualBackEnd1Span.calls.size()
        def actualBackEnd3Span = actualBackEnd1Span.calls.find { it.service.equals("back-end-3") }
        null != actualBackEnd3Span
        def actualBackEnd2Span = actualRootSpan.calls.find { it.service.equals("back-end-2") }
        null != actualBackEnd2Span
    }
}
