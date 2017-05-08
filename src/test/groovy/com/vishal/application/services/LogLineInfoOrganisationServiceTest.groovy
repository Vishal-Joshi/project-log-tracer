package com.vishal.application.services

import com.vishal.application.converters.LogLineInfoToSpanMetaDataConverter
import com.vishal.application.entity.LogLineInfo
import com.vishal.application.entity.Span
import com.vishal.application.entity.SpanMetaData
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Specification


class LogLineInfoOrganisationServiceTest extends Specification {

    LogLineInfoToSpanMetaDataConverter mockLogLineInfoToSpanMetaDataConverter = Mockito.mock(LogLineInfoToSpanMetaDataConverter.class)

    def "should be able to build a map of LogLineInfo sharing caller span ids"(){
        given:
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

        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService(mockLogLineInfoToSpanMetaDataConverter)

        when:
        def mapOfCallerIdsAndLogLineInfo = lineInfoOrganisationService.buildMapOfCallerSpanIdsVsLogLineInfo(traceLogInfos)

        then:
        null != mapOfCallerIdsAndLogLineInfo
        3 == mapOfCallerIdsAndLogLineInfo.size()
        mapOfCallerIdsAndLogLineInfo.get("null").containsAll([rootSpan])
        mapOfCallerIdsAndLogLineInfo.get("aa").containsAll([backEnd1Span, backEnd2Span])
        mapOfCallerIdsAndLogLineInfo.get("ac").containsAll([backEnd3Span])

    }

    def "should be able to build a map of spans each with their own unique span ids"(){
        given:
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

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(rootSpan))
                .thenReturn(new SpanMetaData(new Span(rootSpan.service, rootSpan.start, rootSpan.end, null), "aa"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd1Span))
                .thenReturn(new SpanMetaData(new Span(backEnd1Span.service, backEnd1Span.start, backEnd1Span.end, null), "ac"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd2Span))
                .thenReturn(new SpanMetaData(new Span(backEnd2Span.service, backEnd2Span.start, backEnd2Span.end, null), "ab"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd3Span))
                .thenReturn(new SpanMetaData(new Span(backEnd3Span.service, backEnd3Span.start, backEnd3Span.end, null), "ad"))

        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService(mockLogLineInfoToSpanMetaDataConverter)

        when:
        def mapOfSpanIdsAndSpan = lineInfoOrganisationService.buildMapOfSpanIdsVsSpan(traceLogInfos)

        then:
        null != mapOfSpanIdsAndSpan
        4 == mapOfSpanIdsAndSpan.size()
        mapOfSpanIdsAndSpan.get(rootSpan.spanId)
        mapOfSpanIdsAndSpan.containsKey(backEnd1Span.spanId)
        mapOfSpanIdsAndSpan.containsKey(backEnd2Span.spanId)
        mapOfSpanIdsAndSpan.containsKey(backEnd3Span.spanId)

    }

}
