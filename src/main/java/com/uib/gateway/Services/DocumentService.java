package com.uib.gateway.Services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Entities.Document;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Repositories.DocumentRepository;
import com.uib.gateway.Repositories.CustomerRepository;

import ch.qos.logback.core.util.FileUtil;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository dr;

    @Autowired
    private CustomerRepository cr;

    private final String storageDir = "C:\\Users\\ARIDHI\\Desktop\\PFE\\DocumentStorage\\";

    public String getFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) 
            return fileName;
                int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) 
            return fileName;
        return fileName.substring(0, lastDotIndex);
    }

    public String getFileExtension(String fileName)
    {
        String extension;
        if (fileName == null || fileName.isEmpty()) 
            return fileName;
        int lastDotIndex = fileName.lastIndexOf('.');
        extension = fileName.substring(lastDotIndex, fileName.length());
        return extension;
    }

    public boolean documentExists(Customer customer, DocumentType type)
    {
        return (dr.findByCustomerAndType(customer, type) != null);
    }

    public ResponseEntity<String> uploadDoc(MultipartFile file, DocumentType type, Customer customer) throws IOException
    {
        String customerDir = storageDir + File.separator + customer.getId() + File.separator;

        // create directory if it doesn't exist
        if(!Files.exists(Paths.get(customerDir)))
            Files.createDirectory(Paths.get(customerDir));

        // delete document if it already exist
        if(dr.existsByCustomerAndType(customer, type))
        {
            Files.deleteIfExists(new File(customerDir+type+getFileExtension(file.getOriginalFilename())).toPath());
            dr.delete(dr.findByCustomerAndType(customer, type));
        }

        // check if file is attached
        if(file == null || file.isEmpty())
            return ResponseEntity.badRequest().body("no file was attached");

        Document document = new Document(
            null,
            customer,
            getFileExtension(file.getOriginalFilename()),
            type
        );
        file.transferTo(new File(customerDir + type + document.getExtension()));
        dr.save(document);
        return ResponseEntity.ok().body("file stored successfully");
    }

    // file download method
    public ResponseEntity<?> downloadDoc(Customer customer, DocumentType type) throws IOException
    {
        Document document  = dr.findByCustomerAndType(customer, type);
        if(document == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("file doesn't exist");
            
        String path = storageDir + "/" + customer.getId() + "/" + document.getType() + document.getExtension();
        byte[] file = Files.readAllBytes(new File(path).toPath());
        String extension;
        switch (document.getExtension()) {
            case ".png":
                extension = "image/png"; break;
            case ".jpeg":
            case ".jpg":
                extension = "image/jpeg"; break;
            case ".pdf":
                extension = "application/pdf"; break;
/*             case ".txt":
                extension = "text/plain"; break; */
            default:
                extension = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.valueOf(extension)).body(file);
    }



    public String deleteCustomerDir(Long id){
        try
        {
            org.aspectj.util.FileUtil.deleteContents(new File(storageDir + File.separator + id.toString()));
            Files.deleteIfExists(Paths.get(storageDir + File.separator + id.toString()));
            return "customer files deleted!";
        }
        catch(IOException e)
        {return e.getMessage();}
    }
}
