package com.uib.gateway.Entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uib.gateway.Enums.CustomerIdentityType;
import com.uib.gateway.Enums.RiskLevel;
import com.uib.gateway.Enums.Role;
import com.uib.gateway.Enums.UserStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"documents"})
public class User implements UserDetails{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(unique = true)
    @Email(message = "email invalid! try again.")
    @NotNull(message = "Email is required")
    @NotBlank
    private String email;

    @NotNull(message = "Username reuired")
    @NotBlank
    String username;

    @NotNull @NotBlank
    String password;

    //@JsonIgnore
    //String role;
    @Enumerated(EnumType.STRING)
    Role role;

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

    private String address; 

    @Enumerated(EnumType.STRING)
    private UserStatus status; 

    @Enumerated(EnumType.STRING)
    private CustomerIdentityType identityType;

    @OneToMany(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Document> documents;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Override
    public String getUsername() {
        return this.username;
    }
    
    @Override @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }

    @JsonIgnore
    public String getFullname() {
        if (midName!=null) 
            return this.firstName + " " + this.midName + " " + this.lastName;
        return this.firstName + " " + this.lastName;
        
    }

    @JsonIgnore
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
