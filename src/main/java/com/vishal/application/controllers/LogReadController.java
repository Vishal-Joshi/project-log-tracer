package com.vishal.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogReadController {

    @GetMapping(path = "/readlogs")
    public ResponseEntity readLogs(){
        return null;
    }
}
