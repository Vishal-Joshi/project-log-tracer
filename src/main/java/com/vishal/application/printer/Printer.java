package com.vishal.application.printer;

import com.vishal.application.services.ConsoleOutputService;
import com.vishal.application.services.FileOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Printer {

    private final FileOutputService fileOutputService;
    private final ConsoleOutputService consoleOutputService;

    @Autowired
    public Printer(FileOutputService fileOutputService,
                   ConsoleOutputService consoleOutputService) {
        this.fileOutputService = fileOutputService;
        this.consoleOutputService = consoleOutputService;
    }

    public void print(String content, String outputFileName) {
        fileOutputService.printJson(content, outputFileName);
        consoleOutputService.printJson(content);
    }
}
