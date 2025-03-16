package com.uib.gateway.Services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Entities.CustomerDocument;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Repositories.CustomerDocumentRepository;
import com.uib.gateway.Repositories.CustomerRepository;

@Service
public class CustomerDocumentService {

    @Autowired
    private CustomerDocumentRepository dr;

    @Autowired
    private CustomerRepository cr;

    @Value("${storage.path}")
    private String storageDir;
    private final String allCustomerDir = storageDir + "Customers\\";

    private static SecretKey secretKey;
        static{
            try{
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                secretKey = keyGenerator.generateKey();
        }
        catch(Exception e)
        {System.out.println(e.getMessage());}
    }

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

    public ResponseEntity<String> uploadDoc(MultipartFile file, DocumentType type, Customer customer) throws IOException
    {
        String customerDir = allCustomerDir + File.separator + customer.getId() + File.separator;

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

        CustomerDocument document = new CustomerDocument(
            null,
            customer,
            getFileExtension(file.getOriginalFilename()),
            type
        );



        // encryption
        byte[] encryptedFile;
        try
        {
            encryptedFile = encrypt(file); 
            System.out.println("Encrypted Data: " + Base64.getEncoder().encodeToString(encryptedFile));
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("encryption error: " + e.getMessage());
        }

        File filePath = new File(customerDir + type + ".enc");//document.getExtension());
        FileOutputStream fos = new FileOutputStream(filePath);
        try 
        {
            fos.write(encryptedFile); 
            fos.close();
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("encryption error: " + e.getMessage());
        }

        // save credentials in db
        dr.save(document);
        return ResponseEntity.ok().body("file stored successfully");
    }


    // file download method
    public ResponseEntity<?> downloadDoc(Customer customer, DocumentType type) //throws Exception
    {
        CustomerDocument document  = dr.findByCustomerAndType(customer, type);
        if(document == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("file doesn't exist");
            
        String path = allCustomerDir + "/" + customer.getId() + "/" + document.getType() + ".enc";//document.getExtension();

        //decrypt attempt
        try
        {
            byte[] encryptedFile = Files.readAllBytes(new File(path).toPath());
                //System.out.println("Encrypted Data: " + Base64.getEncoder().encodeToString(encryptedFile));
            byte[] file = decrypt(encryptedFile);
            
        String contentType;
        switch (document.getExtension()) {
            case ".png":
                contentType = "image/png"; break;
            case ".jpeg":
            case ".jpg":
                contentType = "image/jpeg"; break;
            case ".pdf":
                contentType = "application/pdf"; break;
            default:
                contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.valueOf(contentType)).body(file);
    }
    catch(Exception e)
    {        return ResponseEntity.badRequest().body(e.getMessage());    }
    }



    public String deleteCustomerDir(Long id){
        try
        {
            org.aspectj.util.FileUtil.deleteContents(new File(allCustomerDir + File.separator + id.toString()));
            Files.deleteIfExists(Paths.get(allCustomerDir + File.separator + id.toString()));
            return "customer files deleted!";
        }
        catch(IOException e)
        {return e.getMessage();}
    }

    public static byte[] encrypt(MultipartFile data) //throws Exception 
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES");
                //System.out.println("Secret Key: " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data.getBytes());
        }
        catch(Exception e)
        {return null;}
    }

    public static byte[] decrypt(byte[] data) //throws Exception 
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES");
                //System.out.println("Secret Key: " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        }
        catch(Exception e)
        {return null;}
        
    }
}
