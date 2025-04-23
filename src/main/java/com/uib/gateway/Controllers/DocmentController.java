package com.uib.gateway.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.User;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Repositories.UserRepository;
import com.uib.gateway.Services.DocumentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("document")
public class DocmentController {

    @Autowired
    DocumentService documentService;

    @Autowired
    UserRepository userRepository;

    /* @PostMapping("/uploadFile")
    public ResponseEntity<?> upload(@RequestParam MultipartFile file, @RequestParam DocType type) throws IllegalStateException, IOException {
        return documentService.uploadDoc(file, type, null);
    } */

    @PostMapping("/ocr")
    public String ocr(@RequestParam MultipartFile image) {
        //TODO: process POST request
        
        return documentService.scanImage(image);
    }
    
   

    @GetMapping("/downloadFile/{customerId}/{type}")
    public ResponseEntity<?> downloadDoc(@PathVariable Long customerId, @PathVariable DocumentType type){

        User c = userRepository.findById(customerId).orElse(null);
        return documentService.downloadDoc(c, type);
    }
}
