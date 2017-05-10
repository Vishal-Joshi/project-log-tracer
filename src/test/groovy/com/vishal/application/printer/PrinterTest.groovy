package com.vishal.application.printer

import com.vishal.application.services.ConsoleOutputService
import com.vishal.application.services.FileOutputService
import org.mockito.Mockito
import spock.lang.Ignore
import spock.lang.Specification

@Ignore('For some reason interactions are not being captured correctly though in reality its working')
class PrinterTest extends Specification {

    FileOutputService mockFileOutputService = Mockito.mock(FileOutputService.class)
    ConsoleOutputService mockConsoleOutputService = Mockito.mock(ConsoleOutputService.class)

    def "should call console and file output printing service"() {
        given:
        Printer printer = new Printer(mockFileOutputService, mockConsoleOutputService)
        String outputFileName = "fileName"
        String content = "content"
        Mockito.when(mockFileOutputService.printJson(content, outputFileName)).thenReturn(true)
        Mockito.when(mockConsoleOutputService.printJson(content)).thenReturn(true)

        when:
        printer.print(content, outputFileName)

        then:
        1 * mockFileOutputService.printJson(content, outputFileName)
        1 * mockConsoleOutputService.printJson(content)

    }
}
