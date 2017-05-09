package com.vishal.application.services

import com.vishal.application.entity.LogLineInfo
import com.vishal.application.entity.Span
import com.vishal.application.entity.Trace
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Specification

/**
 * Created by vishal.joshi on 5/8/17.
 */
class TraceOrderingServiceTest extends Specification {

    LogLineInfoOrganisationService mockLogLineInfoOrganisationService = Mockito.mock(LogLineInfoOrganisationService.class)

    def "should order traces based earliest finishing span"() {

        given:
        def now = DateTime.now()

        def backEnd2StartDate = now.plusSeconds(10)
        def backEnd2Span = Span
                .builder()
                .service("back-end-2")
                .start(backEnd2StartDate)
                .end(backEnd2StartDate.plusSeconds(2))
                .calls(new ArrayList<Span>())
                .build()

        def backEnd1StartDate = backEnd2StartDate.plusSeconds(10)
        def backEnd3StartDate = backEnd1StartDate.plusSeconds(10)
        def backEnd3Span = Span
                .builder()
                .service("back-end-3")
                .start(backEnd3StartDate)
                .end(backEnd3StartDate.plusSeconds(2))
                .calls(new ArrayList<Span>())
                .build()

        def backEnd1Span = Span
                .builder()
                .service("back-end-1")
                .start(backEnd1StartDate)
                .end(backEnd1StartDate.plusSeconds(2))
                .calls([backEnd3Span])
                .build()

        def rootSpan = Span
                .builder()
                .service("front-end")
                .start(now)
                .end(now.plusSeconds(2))
                .calls([backEnd2Span, backEnd1Span])
                .build()

        Trace trace1 = Trace.builder().id("trace-1").root(rootSpan).build()

        def nowPlus2Seconds = now.plusSeconds(2)

        def backEnd2StartDateForTrace2 = nowPlus2Seconds.plusSeconds(10)
        def backEnd2SpanForTrace2 = Span
                .builder()
                .service("back-end-2")
                .start(backEnd2StartDateForTrace2)
                .end(backEnd2StartDateForTrace2.plusSeconds(2))
                .calls(new ArrayList<Span>())
                .build()

        def backEnd1StartDateForTrace2 = backEnd2StartDateForTrace2.plusSeconds(10)
        def backEnd3StartDateForTrace2 = backEnd1StartDateForTrace2.plusSeconds(10)
        def backEnd3SpanForTrace2 = Span
                .builder()
                .service("back-end-3")
                .start(backEnd3StartDateForTrace2)
                .end(backEnd3StartDateForTrace2.plusSeconds(2))
                .calls(new ArrayList<Span>())
                .build()

        def backEnd1SpanForTrace2 = Span
                .builder()
                .service("back-end-1")
                .start(backEnd1StartDateForTrace2)
                .end(backEnd1StartDateForTrace2.plusSeconds(2))
                .calls([backEnd3SpanForTrace2])
                .build()

        def rootSpanForTrace2 = Span
                .builder()
                .service("front-end")
                .start(nowPlus2Seconds)
                .end(nowPlus2Seconds.plusSeconds(2))
                .calls([backEnd2SpanForTrace2, backEnd1SpanForTrace2])
                .build()

        Trace trace2 = Trace.builder().id("trace-2").root(rootSpanForTrace2).build()

        def traceList = [trace2, trace1]

        // loglineInfos for above spans
        //trace 1
        def rootLogLineInfo = LogLineInfo
                .builder()
                .start(now)
                .end(now.plusSeconds(2))
                .service("front-end")
                .callerSpan("null")
                .spanId("aa")
                .build()

        def backEnd2LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd2StartDate)
                .end(backEnd2StartDate.plusSeconds(2))
                .service("back-end-2")
                .callerSpan("aa")
                .spanId("ab")
                .build()

        def backEnd1LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd1StartDate)
                .end(backEnd1StartDate.plusSeconds(2))
                .service("back-end-1")
                .callerSpan("aa")
                .spanId("ac")
                .build()

        def backEnd3LogLineInfo = LogLineInfo
                .builder()
                .start(backEnd3StartDate)
                .end(backEnd3StartDate.plusSeconds(2))
                .service("back-end-3")
                .callerSpan("ac")
                .spanId("ad")
                .build()

        //trace-2
        def rootLogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(nowPlus2Seconds)
                .end(nowPlus2Seconds.plusSeconds(2))
                .service("front-end-2")
                .callerSpan("null")
                .spanId("aa2")
                .build()

        def backEnd2LogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(backEnd2StartDateForTrace2)
                .end(backEnd2StartDateForTrace2.plusSeconds(2))
                .service("back-end-2-trace-2")
                .callerSpan("aa2")
                .spanId("ab2")
                .build()

        def backEnd1LogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(backEnd1StartDateForTrace2)
                .end(backEnd1StartDateForTrace2.plusSeconds(2))
                .service("back-end-1-trace-2")
                .callerSpan("aa2")
                .spanId("ac2")
                .build()

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

        Mockito.when(mockLogLineInfoOrganisationService.orderTraceIdsByEarliestFinishingSpan(traceIdsVsLogLineInfo)).thenReturn(["trace-1", "trace-2"])

        TraceOrderingService traceOrderingService = new TraceOrderingService(mockLogLineInfoOrganisationService)

        when:
        def orderedTraceList = traceOrderingService.orderByEarliestFinishingSpan(traceList, traceIdsVsLogLineInfo)

        then:
        null != orderedTraceList
        trace1.id == orderedTraceList[0].id
        trace2.id == orderedTraceList[1].id
    }
}
