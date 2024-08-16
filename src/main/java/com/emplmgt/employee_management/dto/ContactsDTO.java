package com.emplmgt.employee_management.dto;

import com.emplmgt.employee_management.enums.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContactsDTO {
    private Long id;
    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    private int assignedTo;

    private int assignedBy;

    private int verifiedBy;

    private int createdBy;

    private Status status;

    private String country;

    private String pinCode;

    private String state;

    private String city;

    private String street;

    private String addressNote;

    private boolean isVerified;

    private boolean qualified;

    private boolean isDeleted = false;

    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ContactLogsDTO> logs;

}
