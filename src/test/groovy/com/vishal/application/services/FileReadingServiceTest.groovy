package com.vishal.application.services

import com.vishal.application.entity.LogLine
import com.vishal.application.entity.LogLineInfo
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
                .thenReturn(new LogLine("eckakaau", new LogLineInfo(new DateTime("2013-10-23T10:12:35.298Z"), new DateTime("2013-10-23T10:12:35.300Z"), "service3", "d6m3shqy", "62d45qeh")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z eckakaau service7 zfjlsiev->d6m3shqy"))
                .thenReturn(new LogLine("eckakaau", new LogLineInfo(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.302Z"), "service7", "zfjlsiev", "d6m3shqy")))

        when:
        Map<String, List<LogLineInfo>> fileContent = fileReadingService.readFile(twoLogLinesFilePath)

        then:
        !fileContent.isEmpty()
        1 == fileContent.size()
        2 == fileContent.get("eckakaau").size()
    }

    def "should be able to create map of different trace Ids"() {
        given:
        String twoLogLinesFilePath = classLoader.getResource("three-log-lines.txt").getFile()
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.298Z 2013-10-23T10:12:35.300Z eckakaau service3 d6m3shqy->62d45qeh"))
                .thenReturn(new LogLine("eckakaau", new LogLineInfo(new DateTime("2013-10-23T10:12:35.298Z"), new DateTime("2013-10-23T10:12:35.300Z"), "service3", "d6m3shqy", "62d45qeh")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z eckakaau service7 zfjlsiev->d6m3shqy"))
                .thenReturn(new LogLine("eckakaau", new LogLineInfo(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.302Z"), "service7", "zfjlsiev", "d6m3shqy")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z abcdef service10 iojlsitb->hjm3shfd"))
                .thenReturn(new LogLine("abcdef", new LogLineInfo(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.302Z"), "service10", "iojlsitb", "hjm3shfd")))

        when:
        Map<String, List<LogLineInfo>> fileContent = fileReadingService.readFile(twoLogLinesFilePath)

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


    def "should return map of trace ids and logLineInfo with logLineInfo ordered by end time"() {
        given:
        String twoLogLinesFilePath = classLoader.getResource("log-lines-unordered-by-end-time.txt").getFile()
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.298Z 2013-10-23T10:12:35.300Z eckakaau service3 d6m3shqy->62d45qeh"))
                .thenReturn(new LogLine("eckakaau", new LogLineInfo(new DateTime("2013-10-23T10:12:35.298Z"), new DateTime("2013-10-23T10:12:35.300Z"), "service3", "d6m3shqy", "62d45qeh")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.302Z eckakaau service7 zfjlsiev->d6m3shqy"))
                .thenReturn(new LogLine("eckakaau", new LogLineInfo(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.302Z"), "service7", "zfjlsiev", "d6m3shqy")))
        Mockito.when(mockTraceLogLineParser.parse("2013-10-23T10:12:35.293Z 2013-10-23T10:12:35.299Z eckakaau service8 cfjlsitb->d7m3shhj"))
                .thenReturn(new LogLine("eckakaau", new LogLineInfo(new DateTime("2013-10-23T10:12:35.293Z"), new DateTime("2013-10-23T10:12:35.299Z"), "service8", "cfjlsitb", "d7m3shhj")))
        when:
        Map<String, List<LogLineInfo>> fileContent = fileReadingService.readFile(twoLogLinesFilePath)

        then:
        fileContent.get("eckakaau")[0].spanId == "d7m3shhj"
        fileContent.get("eckakaau")[1].spanId == "62d45qeh"
        fileContent.get("eckakaau")[2].spanId == "d6m3shqy"
    }
}
