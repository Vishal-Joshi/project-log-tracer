package com.vishal.application

import spock.lang.Specification

class FileReadingServiceTest extends Specification {


    def "test should be able to return non null file content"() {

        given:
        FileReadingService fileReadingService = new FileReadingService()
        ClassLoader classLoader = getClass().getClassLoader();
        def filePath = classLoader.getResource("small-log.txt").getFile()

        when:
        def fileContent = fileReadingService.readFile(filePath);

        then:
        fileContent != null

    }

    def "test should throw exception if file does not exist"() {

        given:
        FileReadingService fileReadingService = new FileReadingService()
        ClassLoader classLoader = getClass().getClassLoader();
        def filePath = classLoader.getResource("small-log.txt").getFile()

        when:
        fileReadingService.readFile(filePath+"/garbage/path");

        then:
        thrown IOException

    }
}
