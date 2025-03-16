package com.uib.gateway.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Repositories.CustomerRepository;
import com.uib.gateway.Services.CustomerDocumentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class CustomerDocmentController {

    @Autowired
    CustomerDocumentService ds;

    @Autowired
    CustomerRepository cr;

    /* @PostMapping("/uploadFile")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file, @RequestParam DocType type) throws IllegalStateException, IOException {
        return ds.uploadDoc(file, type, null);
    } */
   

    @GetMapping("/downloadFile/{customerId}/{type}")
    public ResponseEntity<?> downloadDoc(@PathVariable Long customerId, @PathVariable DocumentType type) throws Exception {

        Customer c = cr.findById(customerId).orElse(null);
        return ds.downloadDoc(c, type);
    }
}
