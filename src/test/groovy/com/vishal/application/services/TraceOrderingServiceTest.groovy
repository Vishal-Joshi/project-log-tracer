package com.vishal.application.services

import com.vishal.application.entity.Span
import com.vishal.application.entity.Trace
import org.joda.time.DateTime
import spock.lang.Specification

/**
 * Created by vishal.joshi on 5/8/17.
 */
class TraceOrderingServiceTest extends Specification {

    def "should order traces based on their first call span's start date"() {

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

        def nowMinus60Minutes = now.minusMinutes(60)

        def backEnd2StartDateForTrace2 = nowMinus60Minutes.plusSeconds(10)
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
                .start(nowMinus60Minutes)
                .end(nowMinus60Minutes.plusSeconds(2))
                .calls([backEnd2SpanForTrace2, backEnd1SpanForTrace2])
                .build()

        Trace trace2 = Trace.builder().id("trace-2").root(rootSpanForTrace2).build()

        def traceList = [trace1, trace2]

        TraceOrderingService traceOrderingService = new TraceOrderingService()

        when:
        def orderedTraceList = traceOrderingService.orderByFirstCallSpanStartDate(traceList)

        then:
        null != orderedTraceList
        trace2.id == orderedTraceList[0].id
        trace1.id == orderedTraceList[1].id
    }
}
