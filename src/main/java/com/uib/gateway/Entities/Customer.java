package com.uib.gateway.Entities;

import com.uib.gateway.Enums.CustomerIdentityType;
import com.uib.gateway.Enums.CustomerStatus;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Valid
public class Customer {

    @Id 
    @GeneratedValue 
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
    private String mail;

    @Column(unique = true) 
    @NotNull(message = "Phone is required")
    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status; 

    @Enumerated(EnumType.STRING)
    private CustomerIdentityType identityType; 


    /* @OneToMany(mappedBy = "")
    private Long cusRelId;
    private Long cusCtrId;
    private Long cusCtyId;*/

}
