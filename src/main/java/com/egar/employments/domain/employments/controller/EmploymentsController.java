package com.egar.employments.domain.employments.controller;


import com.egar.employments.domain.employments.EmploymentsDto;
import com.egar.employments.domain.employments.service.EmploymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employments")
public class EmploymentsController {

    @Autowired
    private EmploymentsService employmentsService;

    @GetMapping()
    public ResponseEntity<List<EmploymentsDto>> getTaskById(@RequestParam(name = "ids") List<String> Ids) {
        List<EmploymentsDto> employments = employmentsService.getEmployments(Ids);
        return new ResponseEntity<> (employments, HttpStatus.OK);
    }
}
