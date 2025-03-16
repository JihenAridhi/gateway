package com.uib.gateway.Controllers;

import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.CustomerStatus;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Services.CustomerService;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("customer")
public class CustomerController {

    @Autowired
    CustomerService cs;

    @Value("${spring.mail.username}") private String sender;

    @Autowired private JavaMailSender javaMailSender;
    
    @GetMapping("findById/{id}") //@PreAuthorize("hasRole('ADMIN')")
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

    @PutMapping("updateStatus/{id}")
    public String verifyCustomer(@PathVariable Long id, @RequestParam CustomerStatus status) {
        Customer c = cs.findCustomerById(id);
        return cs.updateCustomerStatus(c, status);
    }

    @PutMapping("{id}/AML")
    public String AMLScreening(@PathVariable Long id) {
    //@GetMapping("/AML")
    //public String AMLScreening(@RequestBody Customer c) {
        Customer c = cs.findCustomerById(id);
        return cs.AMLScreening(c);
    }
    
    @GetMapping("/mailTest")
    public String sendMail()
    {
        try
        {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sender);
            mail.setTo(sender);
            mail.setSubject("testing gateway");
            mail.setText("hi, this is a tesing text, it's pointless.");

            javaMailSender.send(mail);
            
            return "success";
        }
        catch(Exception e)
        {return "failed: " + e.getMessage();}
    }
}
