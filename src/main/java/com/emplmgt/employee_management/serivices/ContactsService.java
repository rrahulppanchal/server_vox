package com.emplmgt.employee_management.serivices;

import com.emplmgt.employee_management.dto.ChangeAssigneeDTO;
import com.emplmgt.employee_management.dto.ContactLogsDTO;
import com.emplmgt.employee_management.dto.ContactsDTO;
import com.emplmgt.employee_management.dto.ContactsQueryDTO;
import com.emplmgt.employee_management.entities.ContactsEntity;
import com.emplmgt.employee_management.entities.ContactsLogsEntity;
import com.emplmgt.employee_management.entities.UsersEntity;
import com.emplmgt.employee_management.enums.NotificationCategory;
import com.emplmgt.employee_management.enums.Status;
import com.emplmgt.employee_management.enums.UserRole;
import com.emplmgt.employee_management.mappers.ContactMapper;
import com.emplmgt.employee_management.repositories.ContactLogsRepository;
import com.emplmgt.employee_management.repositories.ContactsRepository;
import com.emplmgt.employee_management.repositories.Impl.ContactsSpecification;
import com.emplmgt.employee_management.repositories.UsersRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.lang.reflect.Method;
import java.util.*;

@Service
public class ContactsService {
    final ContactsRepository contactsRepository;
    final ContactLogsRepository contactLogsRepository;
    final UsersRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(ContactsService.class);

    final ContactMapper contactMapper;

    public ContactsService(
            ContactsRepository contactsRepository, ContactLogsRepository contactLogsRepository,
            UsersRepository userRepository, ContactMapper contactMapper, EmailService emailService,
            NotificationService notificationService) {
        this.contactsRepository = contactsRepository;
        this.contactLogsRepository = contactLogsRepository;
        this.userRepository = userRepository;
        this.contactMapper = contactMapper;
        this.emailService = emailService;
        this.notificationService = notificationService;
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
                String title = "Contact created";
                String description = userDetails.getFirstName() + " " + userDetails.getLastName() + " "
                        + "created this contact";
                logData.setDescription(description);
                logData.setTitle(title);
                logData.setContactId(element.getId());
                logData.setActionId(Math.toIntExact(userDetails.getId()));
                createLog(logData);
            });

            return new ResponseEntity<>("Contacts created successfully !!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong while creating contact, try again ??",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> UploadCSV(MultipartFile file, int created, int assigned)
            throws IOException, CsvValidationException {
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
                    String title = "Contact imported ";
                    String description = userDetails.getFirstName() + " " + userDetails.getLastName() + " "
                            + "imported this contact";
                    logData.setDescription(description);
                    logData.setTitle(title);
                    logData.setContactId(savedData.getId());
                    logData.setActionId(Math.toIntExact(userDetails.getId()));
                    createLog(logData);
                }
            }
            return new ResponseEntity<>("Data imported successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong while creating contact, try again ??",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> updateContact(ContactsDTO contactsDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity userDetails = userRepository.findUserByEmail(authentication.getName());
            ContactsEntity contact = this.contactsRepository.findByIdAndIsDeletedFalse(contactsDTO.getId());

            String title = String.format("%s %s updated the contact", userDetails.getFirstName(),
                    userDetails.getLastName());
            String description = buildDescription(contact, contactsDTO);

            if (!description.isEmpty()) {
                ContactsLogsEntity logData = new ContactsLogsEntity();
                logData.setDescription(description);
                logData.setTitle(title);
                logData.setContactId(contact.getId());
                logData.setActionId(Math.toIntExact(userDetails.getId()));
                createLog(logData);
            }

            ContactsEntity payload = updateValue(contactsDTO);

            this.contactsRepository.save(payload);

            return new ResponseEntity<>("Contacts updated successfully !!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> changeAssignee(ChangeAssigneeDTO changeAssigneeDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity assignedByUser = userRepository.findUserByEmail(authentication.getName());

            Optional<UsersEntity> assignedToUserOptional = userRepository.findById(changeAssigneeDTO.getAssignee());

            if (assignedToUserOptional.isPresent()) {
                UsersEntity assignedToUser = assignedToUserOptional.get();

                changeAssigneeDTO.getContacts().forEach(element -> {
                    ContactsEntity contact = this.contactsRepository.findByIdAndIsDeletedFalse(element);
                    contact.setAssignedBy(Math.toIntExact(assignedByUser.getId()));
                    contact.setAssignedTo(Math.toIntExact(assignedToUser.getId()));
                    this.contactsRepository.save(contact);

                    ContactsLogsEntity logData = new ContactsLogsEntity();
                    logData.setTitle("Ownership changed");
                    logData.setDescription(String.format("%s %s assigned contact to %s %s",
                            assignedByUser.getFirstName(), assignedByUser.getLastName(), assignedToUser.getFirstName(),
                            assignedToUser.getLastName()));
                    logData.setContactId(contact.getId());
                    logData.setActionId(Math.toIntExact(assignedByUser.getId()));
                    createLog(logData);
                    notificationService.sendNotification(contact.getId(), assignedToUser.getId(),
                            assignedByUser.getId(), "Contact assigned to you.", logData.getDescription(),
                            NotificationCategory.CONTACT);

                });

                String message = String.format("Contacts assigned to %s %s", assignedToUser.getFirstName(),
                        assignedToUser.getLastName());
                return ResponseEntity.ok(message);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no such user available");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Error :%s", e.getMessage()));
        }
    }

    public ResponseEntity<String> contactAction(ChangeAssigneeDTO changeAssigneeDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity assignedByUser = userRepository.findUserByEmail(authentication.getName());

            changeAssigneeDTO.getContacts().forEach(element -> {
                ContactsEntity contact = this.contactsRepository.findByIdAndIsDeletedFalse(element);
                if (changeAssigneeDTO.getStatus() != null) {
                    ContactsLogsEntity logData = new ContactsLogsEntity();
                    String prev_status = getStatusString(contact.getStatus());
                    String status = getStatusString(changeAssigneeDTO.getStatus());
                    String title = "Status updated ";
                    String description = String.format("%s %s changed the status of this contact from %s to %s",
                            assignedByUser.getFirstName(),
                            assignedByUser.getLastName(),
                            prev_status,
                            status);
                    logData.setDescription(description);
                    logData.setTitle(title);
                    logData.setContactId(element);
                    contact.setStatus(changeAssigneeDTO.getStatus());
                    createLog(logData);
                }
                if (changeAssigneeDTO.getQualified() != null) {
                    ContactsLogsEntity logData = new ContactsLogsEntity();
                    String title = "Contact qualification ";
                    String description = assignedByUser.getFirstName() + " " + assignedByUser.getLastName() + " "
                            + "changed the qualification status of this contact from contact to lead.";
                    logData.setDescription(description);
                    logData.setTitle(title);
                    logData.setContactId(element);
                    contact.setQualified(changeAssigneeDTO.getQualified());
                    logData.setActionId(Math.toIntExact(assignedByUser.getId()));
                    createLog(logData);
                }
                this.contactsRepository.save(contact);
            });

            return ResponseEntity.status(HttpStatus.OK).body("Action performed !!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Error :%s", e.getMessage()));
        }
    }

    public ResponseEntity<?> getContacts(ContactsQueryDTO payload) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity userDetails = userRepository.findUserByEmail(authentication.getName());
            Pageable pageable = PageRequest.of(payload.getPage(), payload.getSize());

            Specification<ContactsEntity> spec = ContactsSpecification.byCriteria(payload, userDetails);

            Page<ContactsEntity> contactsPage = contactsRepository.findAll(spec, pageable);
            return new ResponseEntity<>(contactsPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getContact(Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity userDetails = userRepository.findUserByEmail(authentication.getName());
            ContactsEntity contact;

            if (userDetails.getUserRole() == UserRole.ADMIN) {
                contact = this.contactsRepository.findByIdAndIsDeletedFalse(id);
            } else {
                contact = this.contactsRepository.findByIdAndAssignedToAndIsDeletedFalse(id,
                        Math.toIntExact(userDetails.getId()));
            }
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
            ContactsEntity contacts = this.contactsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found ?"));
            contacts.setDeleted(true);
            this.contactsRepository.save(contacts);
            return new ResponseEntity<>("Contact deleted successfully !!", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong ??", HttpStatus.BAD_REQUEST);
        }
    }

    public void createLog(ContactsLogsEntity contactsLogsEntity) {
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

    private ContactsEntity updateValue(ContactsDTO contactsDTO) {

        ContactsEntity contact = new ContactsEntity();

        contact.setId(contactsDTO.getId());
        contact.setEmail(contactsDTO.getEmail());
        contact.setFirstName(contactsDTO.getFirstName());
        contact.setLastName(contactsDTO.getLastName());
        contact.setPhone(contactsDTO.getPhone());
        contact.setCountry(contactsDTO.getCountry());
        contact.setPinCode(contactsDTO.getPinCode());
        contact.setState(contactsDTO.getState());
        contact.setCity(contactsDTO.getCity());
        contact.setStreet(contactsDTO.getStreet());
        contact.setAddressNote(contactsDTO.getAddressNote());

        return contact;
    }

    private static String buildDescription(Object oldObject, Object newObject) {
        Map<String, String[]> fieldGetters = new HashMap<>();
        fieldGetters.put("Email", new String[] { "getEmail" });
        fieldGetters.put("First Name", new String[] { "getFirstName" });
        fieldGetters.put("Last Name", new String[] { "getLastName" });
        fieldGetters.put("Phone", new String[] { "getPhone" });
        fieldGetters.put("Country", new String[] { "getCountry" });
        fieldGetters.put("Pin Code", new String[] { "getPinCode" });
        fieldGetters.put("State", new String[] { "getState" });
        fieldGetters.put("City", new String[] { "getCity" });
        fieldGetters.put("Street", new String[] { "getStreet" });
        fieldGetters.put("Address Note", new String[] { "getAddressNote" });

        StringBuilder descriptionBuilder = new StringBuilder();
        for (Map.Entry<String, String[]> entry : fieldGetters.entrySet()) {
            String field = entry.getKey();
            String[] getters = entry.getValue();

            try {
                String oldValue = invokeGetter(oldObject, getters[0]);
                String newValue = invokeGetter(newObject, getters[0]);

                if (!Objects.equals(oldValue, newValue)) {
                    if (!descriptionBuilder.isEmpty()) {
                        descriptionBuilder.append("<Br />");
                    }
                    descriptionBuilder.append(field)
                            .append(": ")
                            .append(oldValue)
                            .append(" -> ")
                            .append(newValue);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return descriptionBuilder.toString();
    }

    private static String invokeGetter(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        Object result = method.invoke(obj);
        return result != null ? result.toString() : "";
    }

    private String getStatusString(Status status) {
        return switch (status) {
            case ACTIVE -> "active";
            case IN_ACTIVE -> "in-active";
            case FOLLOW_UP -> "follow-up";
            case NO_ACTION -> "no-action";
            case VERIFIED -> "verified";
            case UN_VERIFIED -> "un-verified";
        };
    }

}
