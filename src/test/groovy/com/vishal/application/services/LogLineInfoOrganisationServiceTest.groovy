package com.vishal.application.services

import com.vishal.application.ApiConstants
import com.vishal.application.converters.LogLineInfoToSpanMetaDataConverter
import com.vishal.application.entity.LogLine
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

    def "should be able to return map based on caller span ids with all it values sorted in start date in ascending order"() {
        given:
        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService(mockLogLineInfoToSpanMetaDataConverter)
        def now = DateTime.now()
        def rootLogLineInfo = LogLineInfo
                .builder()
                .start(now)
                .end(now.plusSeconds(2))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def backEnd2StartDate = now.plusMinutes(1)
        def backEnd2LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd2StartDate)
                .end(backEnd2StartDate.plusSeconds(2))
                .service("back-end-2")
                .callerSpan("aa")
                .spanId("ab")
                .build()

        def backEnd1StartDate = backEnd2StartDate.plusMinutes(1)
        def backEnd1LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd1StartDate)
                .end(backEnd1StartDate.plusSeconds(2))
                .service("back-end-1")
                .callerSpan("aa")
                .spanId("ac")
                .build()

        def backEnd3StartDate = backEnd1StartDate.plusMinutes(1)
        def backEnd3LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd3StartDate)
                .end(backEnd3StartDate.plusSeconds(2))
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

        when:
        Map<String, List<LogLineInfo>> mapOfCallerSpanIdsVsLogLineInfo = lineInfoOrganisationService.buildMapOfCallerSpanIdsVsLogLineInfo(traceLogInfos)

        then:
        mapOfCallerSpanIdsVsLogLineInfo.get("aa")[0].service == backEnd2Span.service
        mapOfCallerSpanIdsVsLogLineInfo.get("aa")[1].service == backEnd1Span.service
        mapOfCallerSpanIdsVsLogLineInfo.get("ac")[0].service == backEnd3Span.service
    }

    def "should order trace ids based on log line info finishing time"(){
        given:
        //trace-1
        def now = DateTime.now()
        def rootLogLineInfo = LogLineInfo
                .builder()
                .start(now)
                .end(now.plusSeconds(20))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def backEnd2StartDate = now.plusSeconds(2)
        def backEnd2LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd2StartDate)
                .end(backEnd2StartDate.plusSeconds(10))
                .service("back-end-2")
                .callerSpan("aa")
                .spanId("ab")
                .build()

        def backEnd1StartDate = backEnd2StartDate.plusSeconds(1)
        def backEnd1LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd1StartDate)
                .end(backEnd1StartDate.plusSeconds(4))
                .service("back-end-1")
                .callerSpan("aa")
                .spanId("ac")
                .build()

        def backEnd3StartDate = backEnd1StartDate.plusSeconds(1)
        def backEnd3LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd3StartDate)
                .end(backEnd3StartDate.plusSeconds(2))
                .service("back-end-3")
                .callerSpan("ac")
                .spanId("ad")
                .build()

        //trace-2
        def nowMinus1Minute = now.minusMinutes(1)
        def rootLogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(nowMinus1Minute)
                .end(nowMinus1Minute.plusSeconds(20))
                .service("front-end-2")
                .callerSpan("null")
                .spanId("aa2")
                .build()

        def backEnd2StartDateForTrace2 = nowMinus1Minute.plusSeconds(2)
        def backEnd2LogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(backEnd2StartDateForTrace2)
                .end(backEnd2StartDateForTrace2.plusSeconds(10))
                .service("back-end-2-trace-2")
                .callerSpan("aa2")
                .spanId("ab2")
                .build()

        def backEnd1StartDateForTrace2 = backEnd2StartDateForTrace2.plusSeconds(1)
        def backEnd1LogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(backEnd1StartDateForTrace2)
                .end(backEnd1StartDateForTrace2.plusSeconds(4))
                .service("back-end-1-trace-2")
                .callerSpan("aa2")
                .spanId("ac2")
                .build()

        def backEnd3StartDateForTrace2 = backEnd1StartDateForTrace2.plusSeconds(1)
        def backEnd3LogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(backEnd3StartDateForTrace2)
                .end(backEnd3StartDateForTrace2.plusSeconds(2))
                .service("back-end-3-trace-2")
                .callerSpan("ac2")
                .spanId("ad2")
                .build()
        def traceIdsVsLogLineInfo = ["trace-1": [rootLogLineInfo, backEnd1LogLineInfo, backEnd2LogLineInfo, backEnd3LogLineInfo],
                                    "trace-2": [rootLogLineInfoForTrace2, backEnd1LogLineInfoForTrace2, backEnd2LogLineInfoForTrace2, backEnd3LogLineInfoForTrace2]]

        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService(mockLogLineInfoToSpanMetaDataConverter)

        when:
        def traceIdsOrderedByEarliestFinishingSpan = lineInfoOrganisationService.orderTraceIdsByEarliestFinishingSpan(traceIdsVsLogLineInfo)

        then:
        null != traceIdsOrderedByEarliestFinishingSpan
        2 == traceIdsOrderedByEarliestFinishingSpan.size()
        "trace-1" == traceIdsOrderedByEarliestFinishingSpan[1]
        "trace-2" == traceIdsOrderedByEarliestFinishingSpan[0]
    }

}
