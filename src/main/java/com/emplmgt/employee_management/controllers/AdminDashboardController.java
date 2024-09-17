package com.emplmgt.employee_management.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.emplmgt.employee_management.serivices.AdminDashboardService;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/v3/admin")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping(path = "/c-stats")
    public ResponseEntity<?> contactsStats(@RequestParam("start") LocalDateTime startDate,
            @RequestParam("end") LocalDateTime endDate) {
        try {
            return adminDashboardService.contactsStats(startDate, endDate);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/u-stats")
    public ResponseEntity<?> usersStats() {
        try {
            return adminDashboardService.usersStats();
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
