package com.vishal.application.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat
import com.vishal.application.entity.Span
import com.vishal.application.entity.Trace
import com.vishal.application.services.LogReadingService
import org.joda.time.DateTime
import org.mockito.Mockito
import org.springframework.format.datetime.joda.DateTimeFormatterFactory
import spock.lang.Specification

/**
 * Created by vishal.joshi on 5/10/17.
 */
class NewLogReadControllerTest extends Specification {

    String basePath = "/base/path"

    LogReadingService mockLogReadingService = Mockito.mock(LogReadingService.class)

    ObjectMapper objectMapper = new ObjectMapper()

    def setup() {
        objectMapper.registerModule(jacksonJodaModule())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    NewLogReadController newLogReadController = new NewLogReadController(basePath, mockLogReadingService, objectMapper)

    def "should return resultant json string for given file"() {
        given:
        def fileName = "small-log.txt"
        def now = DateTime.now()
        def endDate = now.plusSeconds(2)
        Span rootSpan = Span.builder().service("service1").span("span1").start(now).end(endDate).build()
        Trace trace = Trace.builder().id("traceId1").root(rootSpan).build()
        Span rootSpan2 = Span.builder().service("service12").span("span12").start(now).end(endDate).build()
        Trace trace2 = Trace.builder().id("traceId2").root(rootSpan2).build()
        def traces = [trace, trace2]
        Mockito.when(mockLogReadingService.buildTraceAndSpan(basePath + fileName)).thenReturn(traces)
        String traceJsonAsString = objectMapper.writeValueAsString(trace) + "\r\n" + objectMapper.writeValueAsString(trace2) + "\r\n".trim()

        when:
        def resultantTraceJsonString = newLogReadController.readLogs(fileName)

        then:
        resultantTraceJsonString != null
        resultantTraceJsonString == traceJsonAsString
    }


    private static JodaModule jacksonJodaModule() {
        JodaModule module = new JodaModule()
        DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory()
        module.addSerializer(DateTime.class, new DateTimeSerializer(
                new JacksonJodaFormat(formatterFactory.createDateTimeFormatter()
                        .withZoneUTC())))
        return module
    }
}
