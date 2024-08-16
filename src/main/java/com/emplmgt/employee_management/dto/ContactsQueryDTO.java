package com.emplmgt.employee_management.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ContactsQueryDTO {

    private int page = 0;
    private int size = 5;
    private String search = "";
    private Boolean isContact = false;
    private Boolean isDeleted = false;
    private StatusDTO status;
    private DatesDTO dates;


    @Data
    public static class StatusDTO {
        private Boolean active = false;
        private Boolean followUp = false;
        private Boolean noAction = false;
        private Boolean verified = false;
        private Boolean unVerified = false;
        private Boolean inActive = false;
    }

    @Data
    public static class DatesDTO {
        private Boolean lastYear = false;
        private Boolean lastMonth = false;
        private Boolean lastWeek = false;
        private Boolean lastDay = false;
        private Boolean lastHour = false;
    }

}
