package com.emplmgt.employee_management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_contact_logs")
@Data
public class ContactsLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "action_id")
    private int actionId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    @JsonBackReference
    private ContactsEntity contactsEntity;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
