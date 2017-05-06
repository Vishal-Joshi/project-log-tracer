package com.vishal.application;

import com.vishal.application.entity.LogLine;
import com.vishal.application.parsers.TraceLogLineParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileReadingService {

    private final TraceLogLineParser logLineParser;

    @Autowired
    public FileReadingService(TraceLogLineParser traceLogLineParser) {
        this.logLineParser = traceLogLineParser;
    }

    public List<LogLine> readFile(String filePath) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            return stream
                    .map(logLineParser::parse)
                    .collect(Collectors.toList());
        } catch (IOException iOException) {
            log.error("IOException occurred", iOException);
            throw iOException;
        }
    }
}
