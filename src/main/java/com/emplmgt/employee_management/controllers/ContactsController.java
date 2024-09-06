package com.emplmgt.employee_management.controllers;

import com.emplmgt.employee_management.dto.ContactsDTO;
import com.emplmgt.employee_management.dto.ContactsQueryDTO;
import com.emplmgt.employee_management.serivices.ContactsService;
import com.opencsv.CSVReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/v1/contacts")
public class ContactsController {

    private final ContactsService contactsService;
    private static final Set<String> REQUIRED_HEADERS = Set.of("First Name", "Last Name", "Phone", "Country", "State", "Street", "City", "Pin", "Remarks", "Email");

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

    @PostMapping(path = "/upload/csv")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file, @RequestParam int created, @RequestParam int assigned) {
        try {
            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                List<String[]> allLines = reader.readAll();

                if (allLines.isEmpty()) {
                    return new ResponseEntity<>("CSV file is empty.", HttpStatus.BAD_REQUEST);
                }

                String[] header = allLines.getFirst();

                Set<String> headerSet = new HashSet<>(List.of(header));

                if (!REQUIRED_HEADERS.equals(headerSet)) {
                    return new ResponseEntity<>("Header columns name is not correct.", HttpStatus.BAD_REQUEST);
                }
            }
            return contactsService.UploadCSV(file, created, assigned);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e, HttpStatus.BAD_REQUEST);
        }

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
