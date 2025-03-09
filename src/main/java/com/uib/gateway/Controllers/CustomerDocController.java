package com.uib.gateway.Controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Entities.CustomerDoc;
import com.uib.gateway.Enums.DocType;
import com.uib.gateway.Repositories.CustomerRepo;
import com.uib.gateway.Services.CustomerDocServ;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class CustomerDocController {

    @Autowired
    CustomerDocServ cds;

    @Autowired
    CustomerRepo cr;

    /* @PostMapping("/uploadFile")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file, @RequestParam DocType type) throws IllegalStateException, IOException {
        return cds.uploadDoc(file, type, null);
    } */

    @GetMapping("/downloadFile/{customerId}/{type}")
    public ResponseEntity<?> downloadDoc(@PathVariable Long customerId, @PathVariable DocType type) throws IOException {

        Customer c = cr.findById(customerId).orElse(null);
        return cds.downloadDoc(c, type);
    }
}
