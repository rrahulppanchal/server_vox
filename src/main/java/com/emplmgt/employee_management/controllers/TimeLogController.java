package com.emplmgt.employee_management.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emplmgt.employee_management.dto.TimeLogDTO;
import com.emplmgt.employee_management.serivices.TimeLogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/v1/time")
public class TimeLogController {
    final TimeLogService timeLogService;

    public TimeLogController(TimeLogService timeLogService) {
        this.timeLogService = timeLogService;
    }

    @GetMapping
    public ResponseEntity<?> getTime() {
        try {
            return timeLogService.getTime();
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> addTime(@Valid @RequestBody TimeLogDTO timeLogDTO) {
        try {
            return timeLogService.addTime(timeLogDTO);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e, HttpStatus.BAD_REQUEST);
        }
    }
}
