package com.uib.gateway.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.CustomerStatus;
import com.uib.gateway.Repositories.CustomerRepo;

import jakarta.validation.Valid;

@Service
public class CustomerServ {

    @Autowired
    CustomerRepo cr;

    public Customer findCustomerById(Long id) 
    {return cr.findById(id).orElse(null);}

    public List<Customer> findAllCustomers()
    {return cr.findAll();}

    /* public ResponseEntity<String> addCustomer(@Valid Customer customer)
    {
        try
        {
            Customer c = cr.save(customer);
            return  ResponseEntity.status(HttpStatus.CREATED).body(c.toString());
        }
        catch(RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    } */

    public String deleteCustomer(Long id)
    {
        Customer c = findCustomerById(id);
        if (c == null)
            return "failed";
        return "success";
    }

    public Customer addCustomer(Customer customer) {
        return cr.save(customer);
    }
}
