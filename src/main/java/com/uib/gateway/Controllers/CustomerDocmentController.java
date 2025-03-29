package com.uib.gateway.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Repositories.CustomerRepository;
import com.uib.gateway.Services.CustomerDocumentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class CustomerDocmentController {

    @Autowired
    CustomerDocumentService documentService;

    @Autowired
    CustomerRepository customerRepository;

    /* @PostMapping("/uploadFile")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file, @RequestParam DocType type) throws IllegalStateException, IOException {
        return documentService.uploadDoc(file, type, null);
    } */

    @PostMapping("/ocr")
    public String ocr(@RequestParam MultipartFile image) {
        //TODO: process POST request
        
        return documentService.scanImage(image);
    }
    
   

    @GetMapping("/downloadFile/{customerId}/{type}")
    public ResponseEntity<?> downloadDoc(@PathVariable Long customerId, @PathVariable DocumentType type){

        Customer c = customerRepository.findById(customerId).orElse(null);
        return documentService.downloadDoc(c, type);
    }
}
