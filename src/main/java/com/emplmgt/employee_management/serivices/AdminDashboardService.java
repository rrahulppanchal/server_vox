package com.emplmgt.employee_management.serivices;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.emplmgt.employee_management.dto.TimeLogProjection;
import com.emplmgt.employee_management.enums.Status;
import com.emplmgt.employee_management.enums.TimeLog;
import com.emplmgt.employee_management.repositories.ContactsRepository;
import com.emplmgt.employee_management.repositories.TimeLogRepository;
import com.emplmgt.employee_management.repositories.UsersRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {
    private final ContactsRepository contactsRepository;
    private final UsersRepository usersRepository;
    private final TimeLogRepository timeLogRepository;

    public AdminDashboardService(ContactsRepository contactsRepository, UsersRepository usersRepository,
            TimeLogRepository timeLogRepository) {
        this.contactsRepository = contactsRepository;
        this.usersRepository = usersRepository;
        this.timeLogRepository = timeLogRepository;
    }

    public ResponseEntity<?> contactsStats(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            HashedMap res = new HashedMap();
            res.put("total", contactsRepository.countByIsDeletedFalseAndUpdatedAtBetween(startDate, endDate));
            res.put("active", contactsRepository.countByIsDeletedFalseAndStatusAndUpdatedAtBetween(Status.ACTIVE,
                    startDate, endDate));
            res.put("inActive", contactsRepository.countByIsDeletedFalseAndStatusAndUpdatedAtBetween(Status.IN_ACTIVE,
                    startDate, endDate));
            res.put("followUp", contactsRepository.countByIsDeletedFalseAndStatusAndUpdatedAtBetween(Status.FOLLOW_UP,
                    startDate, endDate));
            res.put("noAction", contactsRepository.countByIsDeletedFalseAndStatusAndUpdatedAtBetween(Status.NO_ACTION,
                    startDate, endDate));
            res.put("unVerified",
                    contactsRepository.countByIsDeletedFalseAndStatusAndUpdatedAtBetween(Status.UN_VERIFIED, startDate,
                            endDate));
            res.put("verified", contactsRepository.countByIsDeletedFalseAndStatusAndUpdatedAtBetween(Status.VERIFIED,
                    startDate, endDate));
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> usersStats() {
        try {
            Map<String, Object> result = new HashMap<>();

            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime now = LocalDateTime.now();

            List<TimeLogProjection> timeData = timeLogRepository.findToday(startOfDay, now);

            Map<String, List<TimeLogProjection>> groupedByEmail = timeData.stream()
                    .collect(Collectors.groupingBy(TimeLogProjection::getEmail));

            long totalTimeInSeconds = 0;
            long totalWorking = 0;
            long totalOnBreak = 0;
            long totalStopped = 0;

            for (Map.Entry<String, List<TimeLogProjection>> entry : groupedByEmail.entrySet()) {
                List<TimeLogProjection> userTimeData = entry.getValue();

                Duration totalWorkingDuration = Duration.ZERO;
                LocalDateTime startTime = null;
                TimeLog active = null;

                for (TimeLogProjection item : userTimeData) {
                    LocalDateTime currentTime = item.getEntry();
                    TimeLog action = item.getAction();

                    switch (action) {
                        case START:
                            startTime = currentTime;
                            active = TimeLog.START;
                            break;
                        case STOP:
                            if (startTime != null) {
                                totalWorkingDuration = totalWorkingDuration
                                        .plus(Duration.between(startTime, currentTime));
                                startTime = null;
                                active = TimeLog.STOP;
                            }
                            break;
                        case BREAK:
                            if (startTime != null) {
                                totalWorkingDuration = totalWorkingDuration
                                        .plus(Duration.between(startTime, currentTime));
                                startTime = null;
                                active = TimeLog.BREAK;
                            }
                            break;
                    }
                }

                if (active == TimeLog.START) {
                    totalWorking++;
                }

                if (active == TimeLog.BREAK) {
                    totalOnBreak++;
                    totalWorking++;
                }

                if (active == TimeLog.STOP) {
                    totalStopped++;
                }

                if (startTime != null) {
                    totalWorkingDuration = totalWorkingDuration.plus(Duration.between(startTime, now));
                }
                totalTimeInSeconds = totalTimeInSeconds + totalWorkingDuration.getSeconds();
            }

            result.put("totalTime", totalTimeInSeconds);
            result.put("totalWorking", totalWorking);
            result.put("totalOnBreak", totalOnBreak);
            result.put("totalStopped", totalStopped);
            result.put("totalUsers", usersRepository.countByIsDeletedFalse());

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
