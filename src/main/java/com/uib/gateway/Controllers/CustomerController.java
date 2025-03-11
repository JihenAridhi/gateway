package com.uib.gateway.Controllers;

import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Services.CustomerService;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("customer")
public class CustomerController {

    @Autowired
    CustomerService cs;
    
    @GetMapping("findById/{id}")
    public Customer findById(@PathVariable Long id) {
        return cs.findCustomerById(id);
    }
    
    @GetMapping("findAll")
    public List<Customer> findAllCustomers()
    {return cs.findAllCustomers();}
    
    
    @PostMapping("add")
    public ResponseEntity<String> addCustomer(@ModelAttribute Customer customer, @RequestParam MultipartFile file, @RequestParam DocumentType type)
    {
        return cs.addCustomer(customer, type, file);
    }

    @PostMapping("{customerId}/addDoc")
    public ResponseEntity<String> addDoc(@PathVariable Long customerId, @RequestParam MultipartFile file, @RequestParam DocumentType type) throws IllegalStateException, IOException {        
        return cs.addDocToCustomer(file, type, customerId);
    }
    

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) throws IOException
    {return cs.deleteCustomer(id);}

}
