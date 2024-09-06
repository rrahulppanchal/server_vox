package com.emplmgt.employee_management.controllers;

import com.emplmgt.employee_management.dto.TimeLogDTO;
import com.emplmgt.employee_management.serivices.TimeLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
