package com.uib.gateway.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.CustomerStatus;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Repositories.CustomerRepository;
import com.uib.gateway.Repositories.UserRepository;

import jakarta.validation.Valid;

@Service
public class CustomerService
{

    @Autowired
    CustomerRepository customerRepository;

    @Autowired 
    UserRepository userRepository;

    @Autowired
    CustomerDocumentService documentService;

    @Autowired
    ControlListService cls;

    public Customer findCustomerById(Long id) 
    {return customerRepository.findById(id).orElse(null);}

    public List<Customer> findAllCustomers()
    {return customerRepository.findAll();}

    public ResponseEntity<String> addCustomer(@Valid Customer customer, DocumentType type, MultipartFile file)
    {
        if(userRepository.existsByEmail(customer.getEmail()))
            return ResponseEntity.badRequest().body("Email exists!!");

        try
        {
            customer.setStatus(CustomerStatus.INACTIVE);
            customer.setFirstName(customer.getFirstName().toUpperCase());
            if (customer.getMidName() != null) 
                customer.setMidName(customer.getMidName().toUpperCase());
            customer.setLastName(customer.getLastName().toUpperCase());
            customer.setRole("CUSTOMER");
            Customer c = customerRepository.save(customer);
            String fileMessage = documentService.uploadDoc(file, type, c).getBody();
            return  ResponseEntity.status(HttpStatus.CREATED).body(c.toString()+"\n" + fileMessage);
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("customer save error: " + e.getClass().toString() + ":\n" + e.getMessage());
        }
    }

    public ResponseEntity<String> deleteCustomer(Long id) 
    {
        if (!customerRepository.existsById(id)) 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("customer deletion failed!");
        customerRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(documentService.deleteCustomerDir(id)+"\ncustomer deletion succeeded!");
    }

    public ResponseEntity<String> addDocToCustomer(MultipartFile file, DocumentType type, Long customerId){
        Customer c = customerRepository.findById(customerId).orElse(null);
        try
        {
            return documentService.uploadDoc(file, type, c);
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body(e.getClass().toString() + ":\n" + e.getMessage());
        }   
    }

    public String updateCustomerStatus(Customer customer, CustomerStatus status) {
        if(customer == null)
            return "customer not found";
        customer.setStatus(status);
        customerRepository.save(customer);
        return "customer status: " + status;
    }

    public String controlListScreening(Customer c) {
        if(cls.nameMatch(c.getAliases()))
        {
            if(c.getStatus() == CustomerStatus.UNDER_REVIEW)
                c.setStatus(CustomerStatus.REJECTED);
            else
                c.setStatus(CustomerStatus.UNDER_REVIEW);
        }
        else
            c.setStatus(CustomerStatus.APPROVED);
        customerRepository.save(c);
        return c.getStatus().toString();
    }

}
