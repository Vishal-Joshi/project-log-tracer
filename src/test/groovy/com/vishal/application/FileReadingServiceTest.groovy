package com.vishal.application

import com.vishal.application.entity.LogLine
import com.vishal.application.entity.Span
import com.vishal.application.parsers.TraceLogLineParser
import org.joda.time.DateTime
import org.mockito.Mockito
import spock.lang.Shared
import spock.lang.Specification

class FileReadingServiceTest extends Specification {

    TraceLogLineParser mockTraceLogLineParser = Mockito.mock(TraceLogLineParser.class)

    FileReadingService fileReadingService = new FileReadingService(mockTraceLogLineParser)

    String filePath

    @Shared ClassLoader classLoader = getClass().getClassLoader()

    def setup() {
        filePath = classLoader.getResource("small-log.txt").getFile()
    }

    def "test should be able to return non null file content"() {

        when:
        def fileContent = fileReadingService.readFile(filePath);

        then:
        fileContent != null

    }

    def "test should throw exception if file does not exist"() {

        when:
        fileReadingService.readFile(filePath + "/garbage/path");

        then:
        thrown IOException

    }

    def "test should return list of log line trace jsons"() {
        given:
        String twoLogLinesFilePath = classLoader.getResource("two-log-lines.txt").getFile()
        Mockito.when(mockTraceLogLineParser.parse(Mockito.anyString()))
                .thenReturn(new LogLine("traceId", new Span(DateTime.now(), DateTime.now().plusMillis(10000), "service", "caller-span", "span")))

        when:
        List<LogLine> fileContent = fileReadingService.readFile(twoLogLinesFilePath)

        then:
        fileContent.isEmpty() == false
        fileContent.size() == 2
    }
}
