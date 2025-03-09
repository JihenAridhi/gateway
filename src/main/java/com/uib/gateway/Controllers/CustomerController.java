package com.uib.gateway.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.DocType;
import com.uib.gateway.Services.CustomerServ;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("customer")
public class CustomerController {

    @Autowired
    CustomerServ cs;
    
    @GetMapping("findById/{id}")
    public Customer findById(@PathVariable Long id) {
        return cs.findCustomerById(id);
    }
    
    @GetMapping("findAll")
    public List<Customer> findAllCustomers()
    {return cs.findAllCustomers();}
    
    
    @PostMapping("addCustomer")
    public ResponseEntity<String> addCustomer(@RequestParam Customer customer, @RequestParam MultipartFile file, @RequestParam DocType type) throws IOException
    {
        return cs.addCustomer(customer, type, file);
    }

    @PostMapping("{customerId}/addDoc")
    public ResponseEntity<String> addDoc(@PathVariable Long customerId, @RequestParam MultipartFile file, @RequestParam DocType type) throws IllegalStateException, IOException {        
        return cs.addDocToCustomer(file, type, customerId);
    }
    

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id)
    {return cs.deleteCustomer(id);}

}
