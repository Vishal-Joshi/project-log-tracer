package com.vishal.application.services

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

    @Shared
    ClassLoader classLoader = getClass().getClassLoader()

    def setup() {
        filePath = classLoader.getResource("small-log.txt").getFile()
    }

    def "should return list of log line trace jsons"() {
        given:
        String twoLogLinesFilePath = classLoader.getResource("two-log-lines.txt").getFile()
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.298Z 2013-10-23T10:12:35.300Z eckakaau service3 d6m3shqy->62d45qeh"))
                .thenReturn(new LogLine("eckakaau", new Span(new DateTime("2013-10-23T10:12:35.298Z"), new DateTime("2013-10-23T10:12:35.300Z"), "service3", "d6m3shqy", "62d45qeh")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z eckakaau service7 zfjlsiev->d6m3shqy"))
                .thenReturn(new LogLine("eckakaau", new Span(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.302Z"), "service7", "zfjlsiev", "d6m3shqy")))

        when:
        Map<String, List<Span>> fileContent = fileReadingService.readFile(twoLogLinesFilePath)

        then:
        !fileContent.isEmpty()
        1 == fileContent.size()
        2 == fileContent.get("eckakaau").size()
    }

    def "should be able to create map of different trace Ids"() {
        given:
        String twoLogLinesFilePath = classLoader.getResource("three-log-lines.txt").getFile()
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.298Z 2013-10-23T10:12:35.300Z eckakaau service3 d6m3shqy->62d45qeh"))
                .thenReturn(new LogLine("eckakaau", new Span(new DateTime("2013-10-23T10:12:35.298Z"), new DateTime("2013-10-23T10:12:35.300Z"), "service3", "d6m3shqy", "62d45qeh")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z eckakaau service7 zfjlsiev->d6m3shqy"))
                .thenReturn(new LogLine("eckakaau", new Span(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.302Z"), "service7", "zfjlsiev", "d6m3shqy")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z abcdef service10 iojlsitb->hjm3shfd"))
                .thenReturn(new LogLine("abcdef", new Span(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.302Z"), "service10", "iojlsitb", "hjm3shfd")))

        when:
        Map<String, List<Span>> fileContent = fileReadingService.readFile(twoLogLinesFilePath)

        then:
        !fileContent.isEmpty()
        2 == fileContent.size()
        2 == fileContent.get("eckakaau").size()
        1 == fileContent.get("abcdef").size()
    }

    def "should throw exception if file does not exist"() {

        when:
        fileReadingService.readFile(filePath + "/garbage/path");

        then:
        thrown IOException

    }
}
