package com.uib.gateway.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.CustomerStatus;
import com.uib.gateway.Enums.DocType;
import com.uib.gateway.Repositories.CustomerRepo;

import jakarta.validation.Valid;

@Service
public class CustomerServ {

    @Autowired
    CustomerRepo cr;

    @Autowired
    CustomerDocServ cds;

    public Customer findCustomerById(Long id) 
    {return cr.findById(id).orElse(null);}

    public List<Customer> findAllCustomers()
    {return cr.findAll();}

    public ResponseEntity<String> addCustomer(@Valid Customer customer, DocType type, MultipartFile file)
    {
        try
        {
            customer.setStatus(CustomerStatus.INACTIVE);
            Customer c = cr.save(customer);
            return  ResponseEntity.status(HttpStatus.CREATED).body(c.toString()+"\n"+cds.uploadDoc(file, type, c).getBody());
        }
        catch(IOException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<String> deleteCustomer(Long id) 
    {
        if (!cr.existsById(id)) 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("customer deletion failed!");
        cr.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(cds.deleteCustomerDocs(id)+"\ncustomer deletion succeeded!");
    }

    public ResponseEntity<String> addDocToCustomer(MultipartFile file, DocType type, Long customerId) throws IllegalStateException, IOException {
        Customer c = cr.findById(customerId).orElse(null);
        return cds.uploadDoc(file, type, c);
    }
}
