package com.finance.financial_management_app;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HelloStarterController {

    @GetMapping("/")
    public String helloGreetingRoute() {
        return "Greeting! I hope you're doing well today! Welcome to my Financial Management App! :-D";
    }
    
    
}
