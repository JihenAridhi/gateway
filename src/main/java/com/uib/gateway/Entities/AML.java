package com.uib.gateway.Entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @Entity @NoArgsConstructor
public class AML {
    @Id
    Long id;

    private String name;
    private String type; // INDIVIDUAL, ORGANIZATION, COUNTRY
    private String reason;
    private String source;
    private LocalDate dateAdded;
    private String nationality;

}
