package com.uib.gateway.Entities;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Entity @NoArgsConstructor
public class ControlList {
    @Id
    Long id;

    private String name;
    private Date dateOfBirth;
    private String type; // INDIVIDUAL, ORGANIZATION
    private String reason; // AML, PEP, SANCTIONS
    private String source; // UN, EU, CTAF ...
    private LocalDate dateAdded;
    private String nationality;

}
