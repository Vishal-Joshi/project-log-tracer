package com.vishal.application.services

import com.vishal.application.entity.LogLineInfo
import org.joda.time.DateTime
import spock.lang.Specification


class LogLineInfoOrganisationServiceTest extends Specification {


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

        LogLineInfoOrganisationService lineInfoOrganisationService = new LogLineInfoOrganisationService()

        when:
        def mapOfCallerIdsAndLogLineInfo = lineInfoOrganisationService.buildMapOfLogLineRelatedByCallerSpan(traceLogInfos)

        then:
        null != mapOfCallerIdsAndLogLineInfo
        3 == mapOfCallerIdsAndLogLineInfo.size()
        mapOfCallerIdsAndLogLineInfo.get("null").containsAll([rootSpan])
        mapOfCallerIdsAndLogLineInfo.get("aa").containsAll([backEnd1Span, backEnd2Span])
        mapOfCallerIdsAndLogLineInfo.get("ac").containsAll([backEnd3Span])

    }

}
