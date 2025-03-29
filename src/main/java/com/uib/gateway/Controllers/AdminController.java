package com.uib.gateway.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uib.gateway.Entities.Admin;
import com.uib.gateway.Entities.User;
import com.uib.gateway.Services.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/findAllUsers")
    public List<User> getMethodName() {
        return adminService.findAllUsers();
    }
    

    @PostMapping("/add")
    public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {        
        return adminService.add(admin);
    }
    
    

}
