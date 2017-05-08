package com.vishal.application.services

import com.vishal.application.ApiConstants
import com.vishal.application.converters.LogLineInfoToSpanMetaDataConverter
import com.vishal.application.entity.LogLineInfo
import com.vishal.application.entity.Span
import com.vishal.application.entity.SpanMetaData
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Specification


class LogLineInfoOrganisationServiceTest extends Specification {

    LogLineInfoToSpanMetaDataConverter mockLogLineInfoToSpanMetaDataConverter = Mockito.mock(LogLineInfoToSpanMetaDataConverter.class)

    def "should be able to build a map of LogLineInfo sharing caller span ids"() {
        given:
        def rootLogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def backEnd2LogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-2")
                .callerSpan("aa")
                .spanId("ab")
                .build()

        def backEnd1LogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-1")
                .callerSpan("aa")
                .spanId("ac")
                .build()

        def backEnd3LogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-3")
                .callerSpan("ac")
                .spanId("ad")
                .build()

        def traceLogInfos = [rootLogLineInfo, backEnd1LogLineInfo, backEnd2LogLineInfo, backEnd3LogLineInfo]

        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService(mockLogLineInfoToSpanMetaDataConverter)

        when:
        def mapOfCallerIdsAndLogLineInfo = lineInfoOrganisationService.buildMapOfCallerSpanIdsVsLogLineInfo(traceLogInfos)

        then:
        null != mapOfCallerIdsAndLogLineInfo
        3 == mapOfCallerIdsAndLogLineInfo.size()
        mapOfCallerIdsAndLogLineInfo.get("null").containsAll([rootLogLineInfo])
        mapOfCallerIdsAndLogLineInfo.get("aa").containsAll([backEnd1LogLineInfo, backEnd2LogLineInfo])
        mapOfCallerIdsAndLogLineInfo.get("ac").containsAll([backEnd3LogLineInfo])

    }

    def "should be able to build a map of spans each with their own unique span ids"() {
        given:
        def rootLogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def backEnd2LogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-2")
                .callerSpan("aa")
                .spanId("ab")
                .build()

        def backEnd1LogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-1")
                .callerSpan("aa")
                .spanId("ac")
                .build()

        def backEnd3LogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("back-end-3")
                .callerSpan("ac")
                .spanId("ad")
                .build()

        def traceLogInfos = [rootLogLineInfo, backEnd1LogLineInfo, backEnd2LogLineInfo, backEnd3LogLineInfo]


        def rootSpan = Span
                .builder()
                .service(rootLogLineInfo.getService())
                .start(rootLogLineInfo.getStart())
                .end(rootLogLineInfo.getEnd())
                .calls(new ArrayList<Span>())
                .build()

        def backEnd1Span = Span
                .builder()
                .service(backEnd1LogLineInfo.getService())
                .start(backEnd1LogLineInfo.getStart())
                .end(backEnd1LogLineInfo.getEnd())
                .calls(new ArrayList<Span>())
                .build()

        def backEnd2Span = Span
                .builder()
                .service(backEnd2LogLineInfo.getService())
                .start(backEnd2LogLineInfo.getStart())
                .end(backEnd2LogLineInfo.getEnd())
                .calls(new ArrayList<Span>())
                .build()

        def backEnd3Span = Span
                .builder()
                .service(backEnd3LogLineInfo.getService())
                .start(backEnd3LogLineInfo.getStart())
                .end(backEnd3LogLineInfo.getEnd())
                .calls(new ArrayList<Span>())
                .build()

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(rootLogLineInfo))
                .thenReturn(new SpanMetaData(rootSpan, "aa"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd1LogLineInfo))
                .thenReturn(new SpanMetaData(backEnd1Span, "ac"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd2LogLineInfo))
                .thenReturn(new SpanMetaData(backEnd2Span, "ab"))

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(backEnd3LogLineInfo))
                .thenReturn(new SpanMetaData(backEnd3Span, "ad"))

        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService(mockLogLineInfoToSpanMetaDataConverter)

        when:
        def mapOfSpanIdsAndSpan = lineInfoOrganisationService.buildMapOfSpanIdsVsSpan(traceLogInfos)

        then:
        null != mapOfSpanIdsAndSpan
        mapOfSpanIdsAndSpan.containsKey(rootLogLineInfo.spanId)
        mapOfSpanIdsAndSpan.containsKey(backEnd1LogLineInfo.spanId)
        mapOfSpanIdsAndSpan.containsKey(backEnd2LogLineInfo.spanId)
        mapOfSpanIdsAndSpan.containsKey(backEnd3LogLineInfo.spanId)

    }

    def "should add root span dummy object along with other spans"() {
        given:
        def rootLogLineInfo = LogLineInfo
                .builder()
                .start(DateTime.now())
                .end(DateTime.now().plusSeconds(2))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def traceLogInfos = [rootLogLineInfo]

        def rootSpan = Span
                .builder()
                .service(rootLogLineInfo.getService())
                .start(rootLogLineInfo.getStart())
                .end(rootLogLineInfo.getEnd())
                .calls(new ArrayList<Span>())
                .build()

        Mockito.when(mockLogLineInfoToSpanMetaDataConverter.convert(rootLogLineInfo))
                .thenReturn(new SpanMetaData(rootSpan, "aa"))

        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService(mockLogLineInfoToSpanMetaDataConverter)

        when:
        def mapOfSpanIdsAndSpan = lineInfoOrganisationService.buildMapOfSpanIdsVsSpan(traceLogInfos)

        then:
        null != mapOfSpanIdsAndSpan
        2 == mapOfSpanIdsAndSpan.size()
        mapOfSpanIdsAndSpan.containsKey(ApiConstants.TRACE_INITIATOR_SPAN_ID)
        mapOfSpanIdsAndSpan.containsKey(rootLogLineInfo.spanId)

    }

}
