package com.uib.gateway.Entities;

import com.uib.gateway.Enums.DocumentType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor @NoArgsConstructor
public class CustomerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;
    private String extension;
    @Enumerated(EnumType.STRING)
    private DocumentType type;

}
