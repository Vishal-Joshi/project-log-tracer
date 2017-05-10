package com.vishal.application.services

import com.vishal.application.TraceObjectFactory
import com.vishal.application.entity.LogLineInfo
import com.vishal.application.entity.Span
import com.vishal.application.entity.Trace
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Specification

/**
 * Created by vishal.joshi on 5/10/17.
 */
class LogReadingServiceTest extends Specification {

    FileReadingService mockFileReadingService = Mockito.mock(FileReadingService.class)

    SpanOrganisationService mockSpanOrganisationService = Mockito.mock(SpanOrganisationService.class)

    TraceOrderingService mockTraceOrderingService = Mockito.mock(TraceOrderingService.class)

    TraceObjectFactory mockTraceObjectFactory = Mockito.mock(TraceObjectFactory.class)

    LogReadingService logReadingService = new LogReadingService(mockFileReadingService, mockSpanOrganisationService, mockTraceOrderingService, mockTraceObjectFactory)

    def "should read log file and return list of traces with spans"() {
        given:
        String fileName = "log-traces.txt"

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

        def logLineInfoList = [rootLogLineInfo, backEnd1LogLineInfo, backEnd2LogLineInfo, backEnd3LogLineInfo]
        def mapOfTraceIdVsLogLineInfoList = ["traceId1": logLineInfoList]

        Mockito.when(mockFileReadingService.readFile(fileName)).thenReturn(mapOfTraceIdVsLogLineInfoList)

        def rootSpan = Span
                .builder()
                .service(rootLogLineInfo.getService())
                .start(rootLogLineInfo.getStart())
                .end(rootLogLineInfo.getEnd())
                .calls(new ArrayList<Span>())
                .build()

        Mockito.when(mockSpanOrganisationService.organiseRootSpanAndItsChildren(logLineInfoList)).thenReturn(rootSpan)

        Trace trace = Trace.builder().id("traceId1").root(rootSpan).build()

        Mockito.when(mockTraceOrderingService.orderByStartDateOfRootSpan([trace])).thenReturn([trace])

        Mockito.when(mockTraceObjectFactory.createTraceObject("traceId1", rootSpan)).thenReturn(trace)
        def expectedListOfTraces = [trace]

        when:
        def listOfTraces = logReadingService.buildTraceAndSpan(fileName)

        then:
        listOfTraces != null
        listOfTraces[0].id == expectedListOfTraces[0].id
        listOfTraces[0].root == expectedListOfTraces[0].root
    }

    def "should order traces based on start date of root span"() {
        given:
        String fileName = "log-traces.txt"

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

        def logLineInfoList = [rootLogLineInfo, backEnd1LogLineInfo, backEnd2LogLineInfo, backEnd3LogLineInfo]

        def rootSpan = Span
                .builder()
                .service(rootLogLineInfo.getService())
                .start(rootLogLineInfo.getStart())
                .end(rootLogLineInfo.getEnd())
                .calls(new ArrayList<Span>())
                .build()

        Mockito.when(mockSpanOrganisationService.organiseRootSpanAndItsChildren(logLineInfoList)).thenReturn(rootSpan)

        Trace trace = Trace.builder().id("traceId1").root(rootSpan).build()

        def rootLogLineInfoForTrace2 = LogLineInfo
                .builder()
                .start(rootLogLineInfo.getStart().minusSeconds(1))
                .end(DateTime.now().plusSeconds(2))
                .service("front-end-trace-2")
                .callerSpan("null")
                .spanId("aa")
                .build()
        def rootSpan2 = Span
                .builder()
                .start(rootLogLineInfoForTrace2.getStart())
                .end(rootLogLineInfoForTrace2.getEnd())
                .calls(new ArrayList<Span>())
                .build()
        Trace trace2 = Trace.builder().id("traceId2").root(rootSpan2).build()

        def logLineInfosForTrace2 = [rootLogLineInfoForTrace2]
        Mockito.when(mockSpanOrganisationService.organiseRootSpanAndItsChildren(logLineInfosForTrace2)).thenReturn(rootSpan2)


        def mapOfTraceIdVsLogLineInfoList = ["traceId1": logLineInfoList,
                                             "traceId2": logLineInfosForTrace2]

        Mockito.when(mockFileReadingService.readFile(fileName)).thenReturn(mapOfTraceIdVsLogLineInfoList)

        Mockito.when(mockTraceObjectFactory.createTraceObject("traceId1", rootSpan)).thenReturn(trace)
        Mockito.when(mockTraceObjectFactory.createTraceObject("traceId2", rootSpan2)).thenReturn(trace2)

        Mockito.when(mockTraceOrderingService.orderByStartDateOfRootSpan([trace, trace2])).thenReturn([trace2, trace])

        when:
        def listOfTraces = logReadingService.buildTraceAndSpan(fileName)

        then:
        listOfTraces != null
        listOfTraces.size() == 2
        listOfTraces[0].id == trace2.id
        listOfTraces[1].id == trace.id
    }
}
