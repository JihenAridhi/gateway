package com.uib.gateway.Entities;

import com.uib.gateway.Enums.DocumentType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor @NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Customer customer;
    //private String fileName;
    private String extension;
    @Enumerated(EnumType.STRING)
    private DocumentType type;

}
