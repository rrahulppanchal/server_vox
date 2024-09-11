package com.emplmgt.employee_management.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.emplmgt.employee_management.enums.NotificationCategory;

@Entity
@Table(name = "tbl_notification_log")
@Data
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "category")
    private NotificationCategory category;

    @Column(name = "p_id")
    private Long pid;

    @Column(name = "for_id")
    private Long forId;

    @Column(name = "by_id")
    private Long byId;

    @Column(name = "read")
    private boolean read = false;

    @Column(name = "time", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

}
