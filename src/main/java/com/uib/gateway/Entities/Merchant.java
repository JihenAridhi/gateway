package com.uib.gateway.Entities;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uib.gateway.Enums.CustomerIdentityType;
import com.uib.gateway.Enums.CustomerStatus;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Valid
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable=false)
    private Long id;

    @NotNull 
    @NotBlank
    @Size(min = 5)
    private String firstName;

    private String midName;

    @NotNull 
    @NotBlank
    @Size(min = 5)
    private String lastName;

    @Column(unique = true)
    @Email(message = "email invalid! try again.")
    @NotNull(message = "Email is required")
    @NotBlank
    private String email;

    @Column(unique = true) 
    @NotNull(message = "Phone is required")
    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    private String phone;

    @Column(unique = true) 
    @NotNull(message = "Phone is required")
    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    private String CIN;

    private String address;

    /* @OneToMany(mappedBy = "merchant")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<MerchantDocument> documents; */

}
