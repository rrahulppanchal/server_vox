package com.emplmgt.employee_management.controllers;

import com.emplmgt.employee_management.dto.ContactsDTO;
import com.emplmgt.employee_management.dto.ContactsQueryDTO;
import com.emplmgt.employee_management.entities.ContactsEntity;
import com.emplmgt.employee_management.serivices.ContactsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/v1/contacts")
public class ContactsController {

    private final ContactsService contactsService;

    public ContactsController(ContactsService contactsService) {
        this.contactsService = contactsService;
    }


    @PostMapping
    public ResponseEntity<?> createContacts(@RequestBody List<ContactsDTO> contactsDTO) {
        return contactsService.createContacts(contactsDTO);
    }

    @PostMapping(path = "/all")
    public ResponseEntity<?> getContacts(@RequestBody ContactsQueryDTO contactsQueryDTO) {
        return contactsService.getContacts(contactsQueryDTO);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getContact(@PathVariable("id") Long contact_id) {
        return contactsService.getContact(contact_id);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable("id") Long contact_id) {
        return contactsService.deleteContact(contact_id);
    }
}
