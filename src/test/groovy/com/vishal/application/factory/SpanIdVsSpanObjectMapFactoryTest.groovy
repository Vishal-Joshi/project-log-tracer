package com.vishal.application.factory

import com.vishal.application.ApiConstants
import spock.lang.Specification

/**
 * Created by vishal.joshi on 5/8/17.
 */
class SpanIdVsSpanObjectMapFactoryTest extends Specification {

    def "should create map of spanId vs span"() {
        given:
        SpanIdVsSpanObjectMapFactory spanIdVsSpanObjectMapFactory = new SpanIdVsSpanObjectMapFactory()

        when:
        def mapOfSpanIdVsSpan = spanIdVsSpanObjectMapFactory.create()

        then:
        null != mapOfSpanIdVsSpan

    }

    def "should create map with first entry for trace initiator with null as key"() {
        given:
        SpanIdVsSpanObjectMapFactory spanIdVsSpanObjectMapFactory = new SpanIdVsSpanObjectMapFactory()

        when:
        def mapOfSpanIdVsSpan = spanIdVsSpanObjectMapFactory.create()

        then:
        1 == mapOfSpanIdVsSpan.size()
        mapOfSpanIdVsSpan.containsKey(ApiConstants.TRACE_INITIATOR_SPAN_ID)

    }
}
