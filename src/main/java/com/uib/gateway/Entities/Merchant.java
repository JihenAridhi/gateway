package com.uib.gateway.Entities;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Valid
//public class Merchant implements UserDetails
public class Merchant extends User
{

/*     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable=false)
    private Long id; */

    @NotNull 
    @NotBlank
    @Size(min = 5)
    private String businessName;

    @Column(unique = true) 
    @NotNull(message = "Phone is required")
    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    private String phone;

    private String address;

    /* @OneToMany(mappedBy = "merchant")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<MerchantDocument> documents; */

    /* @Column(unique = true)
    @Email(message = "email invalid! try again.")
    @NotNull(message = "Email is required")
    @NotBlank
    private String email;

    String password;

    String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    } */

}
