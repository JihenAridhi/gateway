package com.uib.gateway.Entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uib.gateway.Enums.CustomerIdentityType;
import com.uib.gateway.Enums.CustomerStatus;
import com.uib.gateway.Enums.RiskLevel;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Valid
//public class Customer implements UserDetails
public class Customer extends User
{

/*     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable=false)
    private Long id; */

    @NotNull 
    @NotBlank
    @Size(min = 3)
    private String firstName;

    private String midName;

    @NotNull 
    @NotBlank
    @Size(min = 3)
    private String lastName;

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

    @Enumerated(EnumType.STRING)
    private CustomerStatus status; 

    @Enumerated(EnumType.STRING)
    private CustomerIdentityType identityType;

    @OneToMany(mappedBy = "customer")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<CustomerDocument> documents;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    public List<String> getAliases()
    {
        List<String> aliases = new ArrayList<>();
        aliases.add(this.firstName + " " + this.lastName);
        aliases.add(String.valueOf(this.firstName.charAt(0)) + String.valueOf(this.lastName.charAt(0)));
        aliases.add(this.firstName.charAt(0) + "." + this.lastName.charAt(0) + ".");
        aliases.add(this.firstName + " " + this.lastName.charAt(0));
        aliases.add(this.firstName + " " + this.lastName.charAt(0) + ".");
        aliases.add(this.firstName.charAt(0) + " " + this.lastName);
        aliases.add(this.firstName.charAt(0) + ". " + this.lastName);
        
        if (midName!=null && !midName.isEmpty()) 
        {
            aliases.add(this.firstName + " " + this.midName + " " + this.lastName);
            aliases.add(this.firstName + " " + this.midName.charAt(0) + ". " + this.lastName);
            aliases.add(this.firstName.charAt(0) + ". " + this.midName.charAt(0) + ". " + this.lastName);
        }
        return aliases;
    }

}
