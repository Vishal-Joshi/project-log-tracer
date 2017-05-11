package com.vishal.application.services;

import org.springframework.stereotype.Component;

@Component
public class ConsoleOutputService {

    public boolean printJson(String jsonString) {
        System.out.print(jsonString);
        return true;
    }
}
