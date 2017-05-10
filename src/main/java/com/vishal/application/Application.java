package com.vishal.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class Application {

    @Value("${base.path.trace.json.output}")
    private String basePathForTraceOutput;

    @Value("${base.path}")
    private String basePathForLogFiles;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);

    }

    @PostConstruct
    public void createRequiredDirectories() throws IOException {
        Files.createDirectories(Paths.get(basePathForLogFiles));
        Files.createDirectories(Paths.get(basePathForTraceOutput));
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(jacksonJodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public JodaModule jacksonJodaModule() {
        JodaModule module = new JodaModule();
        DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory();
        module.addSerializer(DateTime.class, new DateTimeSerializer(
                new JacksonJodaFormat(formatterFactory.createDateTimeFormatter()
                        .withZoneUTC())));
        return module;
    }

}
