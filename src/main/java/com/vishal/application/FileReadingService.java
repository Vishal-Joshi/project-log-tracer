package com.vishal.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileReadingService {

    public String readFile(String filePath) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(fileContent::append);
        } catch (IOException iOException) {
            log.error("IOException occurred", iOException);
            throw iOException;
        }
        return fileContent.toString();
    }
}
