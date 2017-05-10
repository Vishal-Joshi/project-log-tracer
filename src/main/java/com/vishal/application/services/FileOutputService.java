package com.vishal.application.services;

import com.vishal.application.exception.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
@Slf4j
public class FileOutputService {

    private String basePathForOutputFiles;

    public FileOutputService(@Value("${base.path.trace.json.output}") String basePathForOutputFiles) {
        this.basePathForOutputFiles = basePathForOutputFiles;
    }

    public boolean printJson(String jsonAsString, String outputFileName) {
        try {
            Files.write(Paths.get(basePathForOutputFiles, outputFileName), jsonAsString.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException ioException) {
            log.error("Error occurred while writing json output", ioException);
            throw new InternalServerError("Error occurred while writing json output", ioException);
        }
        return true;
    }
}
