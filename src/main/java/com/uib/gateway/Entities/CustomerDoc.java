package com.uib.gateway.Entities;

import com.uib.gateway.Enums.DocType;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class CustomerDoc {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Customer customer;

    private DocType type;

}
