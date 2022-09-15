package com.egar.employments.domain.employments.controller;


import com.egar.employments.domain.employments.EmploymentsDto;
import com.egar.employments.domain.employments.service.EmploymentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.egartech.sdk.dto.task.customfield.field.CustomField;

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
