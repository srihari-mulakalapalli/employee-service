package com.company.employeeservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

    @GetMapping("/")
    public String home() {
        return "Employee Service Running - Version 2";
    }

    @GetMapping("/employees")
    public String employees() {
        return "Employee List";
    }
}
