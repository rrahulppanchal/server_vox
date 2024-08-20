package com.emplmgt.employee_management.serivices;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.time.Duration;
import com.emplmgt.employee_management.dto.TimeLogDTO;
import com.emplmgt.employee_management.dto.TimeLogProjection;
import com.emplmgt.employee_management.entities.TimeLogEntity;
import com.emplmgt.employee_management.enums.TimeLog;
import com.emplmgt.employee_management.repositories.TimeLogRepository;

@Service
public class TimeLogService {
    final TimeLogRepository timeLogRepository;

    public TimeLogService(TimeLogRepository timeLogRepository) {
        this.timeLogRepository = timeLogRepository;
    }

    public ResponseEntity<?> getTime() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime now = LocalDateTime.now();

            List<TimeLogProjection> timeData = timeLogRepository.findByEmail(email, startOfDay, now);

            Duration totalWorkingDuration = Duration.ZERO;
            LocalDateTime startTime = null;
            boolean active = false;

            for (TimeLogProjection entity : timeData) {
                LocalDateTime currentTime = entity.getEntry();
                TimeLog action = entity.getAction();

                switch (action) {
                    case START:
                        startTime = currentTime;
                        active = true;
                        break;
                    case STOP:
                    case BREAK:
                        if (startTime != null) {
                            totalWorkingDuration = totalWorkingDuration.plus(Duration.between(startTime, currentTime));
                            startTime = null;
                            active = false;
                        }
                        break;
                }
            }

            if (startTime != null) {
                totalWorkingDuration = totalWorkingDuration.plus(Duration.between(startTime, now));
            }

            return ResponseEntity.ok().body(Map.of(
                    "timeData", timeData,
                    "totalWorkingSeconds", totalWorkingDuration.getSeconds(),
                    "active", active));
        } catch (Exception e) {
            return new ResponseEntity<>("Error: e", HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> addTime(TimeLogDTO timeLogDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TimeLogEntity payload = new TimeLogEntity();
            payload.setUserEmail((authentication.getName()));
            payload.setEntry(LocalDateTime.now());
            payload.setAction(timeLogDTO.getAction());

            timeLogRepository.save(payload);

            String resMessage = "Time logger started.";

            if (timeLogDTO.getAction() == TimeLog.BREAK) {
                resMessage = "User set on the break.";
            }
            if (timeLogDTO.getAction() == TimeLog.STOP) {
                resMessage = "Time logger stopped.";
            }

            return new ResponseEntity<>(resMessage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: ", HttpStatus.BAD_REQUEST);
        }
    }
}
