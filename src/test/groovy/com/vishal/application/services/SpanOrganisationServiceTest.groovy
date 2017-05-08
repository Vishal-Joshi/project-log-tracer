package com.vishal.application.services

import com.vishal.application.entity.LogLineInfo
import com.vishal.application.entity.Span
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Specification

/**
 * Created by vishal.joshi on 5/8/17.
 */
class SpanOrganisationServiceTest extends Specification {

    LogLineInfoOrganisationService mockLogLineInfoOrganisationService = Mockito.mock(LogLineInfoOrganisationService.class);

    def "should be return root span with all its children correctly associated"() {
        given:
        SpanOrganisationService newSpanOrganisationService = new SpanOrganisationService(mockLogLineInfoOrganisationService)
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

        Mockito.when(mockLogLineInfoOrganisationService.buildMapOfCallerSpanIdsVsLogLineInfo(traceLogInfos))
                .thenReturn(["null": [rootLogLineInfo],
                             "aa"  : [backEnd1LogLineInfo, backEnd2LogLineInfo],
                             "ac"  : [backEnd3LogLineInfo]])

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

        def traceInitiatorSpan = Span
                .builder()
                .build()
        Mockito.when(mockLogLineInfoOrganisationService.buildMapOfSpanIdsVsSpan(traceLogInfos))
                .thenReturn(["aa"  : rootSpan,
                             "ab"  : backEnd2Span,
                             "ac"  : backEnd1Span,
                             "ad"  : backEnd3Span,
                             "null": traceInitiatorSpan])

        when:
        Span actualRootSpan = newSpanOrganisationService.organiseRootSpanAndItsChildren(traceLogInfos)

        then:
        null != actualRootSpan
        2 == actualRootSpan.calls.size()
        def actualBackEnd1Span = actualRootSpan.calls.find({ (it.service == "back-end-1") })
        null != actualBackEnd1Span
        null != actualRootSpan.calls.find({ (it.service == "back-end-2") })
        1 == actualBackEnd1Span.calls.size()
        null != actualBackEnd1Span.calls.find({ (it.service == "back-end-3") })
    }

}
