package com.vishal.application.services

import com.vishal.application.exception.InternalServerError
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class FileOutputServiceTest extends Specification {

    String basePath = "/tmp/tracer-json-output/"

    String fileName = "traceid1.txt"

    def setup() {
        Files.createDirectories(Paths.get(basePath))
    }

    def cleanup() {
        Files.deleteIfExists(Paths.get(basePath, fileName))
        Files.deleteIfExists(Paths.get(basePath))
    }

    def "output json content to a file should be successful"() {
        given:
        FileOutputService fileOutputService = new FileOutputService(basePath)

        String traceJson = "{id:trace1}"

        when:
        def isSuccessful = fileOutputService.printJson(traceJson, fileName)

        then:
        isSuccessful
    }

    def "output file should have whole json content written in it"() {
        given:
        FileOutputService fileOutputService = new FileOutputService(basePath)

        String traceJson = "{id:trace1}"

        when:
        fileOutputService.printJson(traceJson, fileName)

        then:
        Files.exists(Paths.get(basePath + fileName))
    }

    def "should throw error if json output to file failed for some reason"() {
        given:
        FileOutputService fileOutputService = new FileOutputService(basePath)

        String traceJson = "{id:trace1}"

        when:
        fileOutputService.printJson(traceJson, "/garbage/" + fileName)

        then:
        thrown(InternalServerError)
    }

    def "should verify that content written to file is correct"() {
        given:
        FileOutputService fileOutputService = new FileOutputService(basePath)

        String traceJson = "{id:trace1}"

        when:
        fileOutputService.printJson(traceJson, fileName)

        then:
        String fileContent = new String(Files.readAllBytes(Paths.get(basePath, fileName)))
        fileContent == traceJson

    }
}
