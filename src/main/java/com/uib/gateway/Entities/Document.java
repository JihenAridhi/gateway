package com.uib.gateway.Entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uib.gateway.Enums.DocumentType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@AllArgsConstructor @NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JsonIgnore
    private User user;
    private String extension;
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    private boolean isVerified;
}
