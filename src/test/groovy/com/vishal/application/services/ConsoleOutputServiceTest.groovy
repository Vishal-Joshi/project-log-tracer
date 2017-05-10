package com.vishal.application.services

import spock.lang.Specification


class ConsoleOutputServiceTest extends Specification {

    def "should be able to print trace json to standard output"(){
        given:
        ConsoleOutputService consoleOutputService = new ConsoleOutputService()
        String traceJson = "{id:traceId1}"
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        when:
        consoleOutputService.printJson(traceJson)

        then:
        traceJson == outContent.toString()

    }
}
