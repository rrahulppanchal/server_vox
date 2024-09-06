package com.emplmgt.employee_management.serivices;

import com.emplmgt.employee_management.dto.ContactLogsDTO;
import com.emplmgt.employee_management.dto.ContactsDTO;
import com.emplmgt.employee_management.dto.ContactsQueryDTO;
import com.emplmgt.employee_management.entities.ContactsEntity;
import com.emplmgt.employee_management.entities.ContactsLogsEntity;
import com.emplmgt.employee_management.entities.UsersEntity;
import com.emplmgt.employee_management.mappers.ContactMapper;
import com.emplmgt.employee_management.repositories.ContactLogsRepository;
import com.emplmgt.employee_management.repositories.ContactsRepository;
import com.emplmgt.employee_management.repositories.Impl.ContactsSpecification;
import com.emplmgt.employee_management.repositories.UsersRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


@Service
public class ContactsService {
    final ContactsRepository contactsRepository;
    final ContactLogsRepository contactLogsRepository;
    final UsersRepository userRepository;

    final ContactMapper contactMapper;

    public ContactsService(
            ContactsRepository contactsRepository, ContactLogsRepository contactLogsRepository, UsersRepository userRepository, ContactMapper contactMapper) {
        this.contactsRepository = contactsRepository;
        this.contactLogsRepository = contactLogsRepository;
        this.userRepository = userRepository;
        this.contactMapper = contactMapper;
    }

    public ContactsDTO convertToDTO(ContactsEntity contactsEntity) {
        return contactMapper.toDTO(contactsEntity);
    }

    public ContactsEntity convertToEntity(ContactsDTO contactsDTO) {
        return contactMapper.toEntity(contactsDTO);
    }

    public List<ContactsDTO> convertToDTOs(List<ContactsEntity> contactsEntity) {
        return contactMapper.toDTOs(contactsEntity);
    }

    public List<ContactsEntity> convertToEntities(List<ContactsDTO> contactsDTO) {
        return contactMapper.toEntities(contactsDTO);
    }

    public List<ContactLogsDTO> convertToContactLogDTO(List<ContactsLogsEntity> contactsLogsEntities) {
        return contactMapper.toLogsDTOs(contactsLogsEntities);
    }

    public ResponseEntity<?> createContacts(List<ContactsDTO> contactsDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity userDetails = userRepository.findUserByEmail(authentication.getName());
            List<ContactsEntity> payload = convertToEntities(contactsDTO);
            List<ContactsEntity> savedData = contactsRepository.saveAll(payload);

            savedData.forEach(element -> {
                ContactsLogsEntity logData = new ContactsLogsEntity();
                String title = userDetails.getFirstName() + " " + userDetails.getLastName() + " created " + element.getFirstName() + " " + "afresh.";
                String description = "First time the contact has been created !";
                logData.setDescription(description);
                logData.setTitle(title);
                logData.setContactId(element.getId());
                logData.setActionId(0);
                createLog(logData);
            });

            return new ResponseEntity<>("Contacts created successfully !!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong while creating contact, try again ??", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> UploadCSV(MultipartFile file, int created, int assigned) throws IOException, CsvValidationException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity userDetails = userRepository.findUserByEmail(authentication.getName());
            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                reader.skip(1);
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    ContactsDTO contact = mapToContact(nextLine, created, assigned);
                    ContactsEntity payload = convertToEntity(contact);
                    ContactsEntity savedData = contactsRepository.save(payload);

                    ContactsLogsEntity logData = new ContactsLogsEntity();
                    String title = userDetails.getFirstName() + " " + userDetails.getLastName() + " imported " + savedData.getFirstName();
                    String description = "First time the contact has been imported !";
                    logData.setDescription(description);
                    logData.setTitle(title);
                    logData.setContactId(savedData.getId());
                    logData.setActionId(0);
                    createLog(logData);
                }
            }
            return new ResponseEntity<>("Data imported successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getContacts(ContactsQueryDTO payload) {
        try {
            Pageable pageable = PageRequest.of(payload.getPage(), payload.getSize());
            System.out.println(payload.getStatus().getActive());

            Specification<ContactsEntity> spec = ContactsSpecification.byCriteria(payload);

            System.out.println(contactsRepository.findAll(spec, pageable));
            Page<ContactsEntity> contactsPage = contactsRepository.findAll(spec, pageable);
            return new ResponseEntity<>(contactsPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getContact(Long id) {
        try {
            ContactsEntity contact = this.contactsRepository.findByIdAndIsDeletedFalse(id);
            ContactsDTO resData = convertToDTO(contact);

            if (resData == null) {
                return new ResponseEntity<>("No contact found ?", HttpStatus.NOT_FOUND);
            }

            List<ContactsLogsEntity> logs = this.contactLogsRepository.findByContactId(id);

            if (!logs.isEmpty()) {
                List<ContactLogsDTO> logData = convertToContactLogDTO(logs);
                resData.setLogs(logData);
            }

            return new ResponseEntity<>(resData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteContact(Long id) {
        try {
            ContactsEntity contacts = this.contactsRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found ?"));
            contacts.setDeleted(true);
            this.contactsRepository.save(contacts);
            return new ResponseEntity<>("Contact deleted successfully !!", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong ??", HttpStatus.BAD_REQUEST);
        }
    }

    private void createLog(ContactsLogsEntity contactsLogsEntity) {
        contactLogsRepository.save(contactsLogsEntity);
    }

    private ContactsDTO mapToContact(String[] data, int created, int assigned) {
        ContactsDTO contact = new ContactsDTO();
        contact.setFirstName(data[0]);
        contact.setLastName(data[1]);
        contact.setEmail(data[2]);
        contact.setPhone(data[3]);
        contact.setCountry(data[4]);
        contact.setState(data[5]);
        contact.setCity(data[6]);
        contact.setStreet(data[7]);
        contact.setPinCode(data[8]);
        contact.setAddressNote(data[9]);
        contact.setCreatedBy(created);
        contact.setAssignedBy(created);
        contact.setAssignedTo(assigned);
        return contact;
    }

}
