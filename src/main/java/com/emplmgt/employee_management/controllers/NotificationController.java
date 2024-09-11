package com.emplmgt.employee_management.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import com.emplmgt.employee_management.dto.SetReminderDTO;
import com.emplmgt.employee_management.entities.ContactsLogsEntity;
import com.emplmgt.employee_management.enums.NotificationCategory;
import com.emplmgt.employee_management.serivices.ContactsService;
import com.emplmgt.employee_management.serivices.NotificationService;

@RestController
@RequestMapping(path = "/v1/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final ContactsService contactsService;

    public NotificationController(NotificationService notificationService, ContactsService contactsService) {
        this.notificationService = notificationService;
        this.contactsService = contactsService;
    }

    @GetMapping(path = "/get-user")
    public ResponseEntity<?> getUserNotification() {
        try {
            return notificationService.getNotification();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/set-reminder")
    @Transactional
    public ResponseEntity<?> setReminder(@RequestBody SetReminderDTO setReminderDTO) {
        try {
            if(setReminderDTO.getCategory() == NotificationCategory.CONTACT){
                ContactsLogsEntity logData = new ContactsLogsEntity();
                logData.setDescription(setReminderDTO.getMessage());
                logData.setTitle(setReminderDTO.getTitle());
                logData.setContactId(setReminderDTO.getPid());
                logData.setActionId(Math.toIntExact(setReminderDTO.getById()));
                contactsService.createLog(logData);
            }
            return notificationService.setReminder(setReminderDTO);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/write")
    public ResponseEntity<?> writeNotification(@RequestParam Long id) {
        try {
            return notificationService.updateNotification(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
