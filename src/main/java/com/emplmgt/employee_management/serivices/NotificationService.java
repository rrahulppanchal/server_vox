package com.emplmgt.employee_management.serivices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.emplmgt.employee_management.dto.SetReminderDTO;
import com.emplmgt.employee_management.entities.NotificationEntity;
import com.emplmgt.employee_management.entities.UsersEntity;
import com.emplmgt.employee_management.enums.NotificationCategory;
import com.emplmgt.employee_management.repositories.NotificationRepository;
import com.emplmgt.employee_management.repositories.UsersRepository;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final UsersRepository usersRepository;

    public NotificationService(NotificationRepository notificationRepository, UsersRepository usersRepository) {
        this.notificationRepository = notificationRepository;
        this.usersRepository = usersRepository;
    }

    public void sendNotification(Long pId, Long forId, Long byId, String title, String message,
            NotificationCategory category) {
        try {
            NotificationEntity payload = new NotificationEntity();
            payload.setPid(pId);
            payload.setForId(forId);
            payload.setById(byId);
            payload.setTitle(title);
            payload.setMessage(message);
            payload.setCategory(category);

            notificationRepository.save(payload);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public ResponseEntity<?> setReminder(SetReminderDTO setReminderDTO) {
        try {
            NotificationEntity payload = new NotificationEntity();
            payload.setPid(setReminderDTO.getPid());
            payload.setForId(setReminderDTO.getForId());
            payload.setById(setReminderDTO.getById());
            payload.setTitle(setReminderDTO.getTitle());
            payload.setMessage(setReminderDTO.getMessage());
            payload.setCategory(setReminderDTO.getCategory());
            payload.setTimestamp(setReminderDTO.getTimestamp());

            notificationRepository.save(payload);
            return new ResponseEntity<>(setReminderDTO.getTitle() + " " + "set", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getNotification() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersEntity userDetails = usersRepository.findUserByEmail(authentication.getName());
            LocalDateTime now = LocalDateTime.now();
            List<NotificationEntity> res = notificationRepository.findByForIdAndTimestampBefore(userDetails.getId(),
                    now);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateNotification(Long id) {
        try {
            NotificationEntity entry = notificationRepository.getById(id);
            entry.setRead(true);
            notificationRepository.save(entry);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
