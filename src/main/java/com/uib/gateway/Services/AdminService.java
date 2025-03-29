package com.uib.gateway.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uib.gateway.Entities.Admin;
import com.uib.gateway.Entities.User;
import com.uib.gateway.Repositories.AdminRepository;
import com.uib.gateway.Repositories.UserRepository;

@Service
public class AdminService //implements UserDetailsService 
//public class AdminService extends UserService
{

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminRepository adminRepository;

    @Autowired 
    UserRepository userRepository;

    public ResponseEntity<String> add(Admin admin)
    {
        if(userRepository.existsByEmail(admin.getEmail()))
            return ResponseEntity.badRequest().body("Email exists!!");

        try {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            admin.setRole("ADMIN");
            return ResponseEntity.ok().body(adminRepository.save(admin).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getClass() + ": " + e.getMessage());
        }
    }

    public List<Admin> findAll(){return adminRepository.findAll();}

    public Admin findAdminByEmail(String email)
    {return adminRepository.findByEmail(email);}

    public List<User> findAllUsers()
    {return userRepository.findAll();}

    

}
