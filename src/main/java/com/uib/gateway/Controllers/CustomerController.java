package com.uib.gateway.Controllers;

import org.springframework.web.bind.annotation.RestController;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Services.CustomerServ;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    
    /* @PostMapping("add")
    public ResponseEntity addCustomer(@RequestBody Customer customer)
    {return cs.addCustomer(customer);} */

    @PostMapping("add")
    public Customer addCustomer(@RequestBody Customer customer)
    {return cs.addCustomer(customer);}

    @DeleteMapping("delete/{id}")
    public String deleteCustomer(@PathVariable Long id)
    {return cs.deleteCustomer(id);}
}
