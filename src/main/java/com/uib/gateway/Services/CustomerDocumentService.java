package com.uib.gateway.Services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

import jakarta.annotation.PostConstruct;
import net.sourceforge.tess4j.Tesseract;

@Service
public class CustomerDocumentService {

    @Autowired
    private CustomerDocumentRepository documentRepository;

    @Autowired
    private CustomerRepository cr;

    @Value("${document.storage.path}")
    private String storageDir ;
    private String allCustomerDir;// = storageDir + "Customers\\";

    @PostConstruct
    public void init() {
        this.allCustomerDir = storageDir + File.separator + "Customers" + File.separator;
    }

    private static SecretKey secretKey;
        static{
            try{
                /* KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                secretKey = keyGenerator.generateKey(); */
                String key = "@myEncryptionKey"; 
                secretKey = new SecretKeySpec(key.getBytes(), "AES");
        }
        catch(Exception e)
        {System.out.println(e.getClass().toString() + ":\n" + e.getMessage());}
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

    public ResponseEntity<String> uploadDoc(MultipartFile file, DocumentType type, Customer customer)
    {
        String customerDir = allCustomerDir + File.separator + customer.getId() + File.separator;

        try
        {
            // create directory if it doesn't exist
            if(!Files.exists(Paths.get(customerDir)))
                Files.createDirectory(Paths.get(customerDir));

            // delete document if it already exist
            if(documentRepository.existsByCustomerAndType(customer, type))
            {
                Files.deleteIfExists(new File(customerDir+type+getFileExtension(file.getOriginalFilename())).toPath());
                documentRepository.delete(documentRepository.findByCustomerAndType(customer, type));
            }
       
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("file storage directory error: " + e.getClass().toString() + ":\n" + e.getMessage());
        }

        // check if file is attached
        if(file == null || file.isEmpty())
            return ResponseEntity.badRequest().body("no file was attached");

        CustomerDocument document = new CustomerDocument(
            null,
            customer,
            //UUID.randomUUID().toString(),
            getFileExtension(file.getOriginalFilename()),
            type,
            false
        );

        // encryption
        byte[] encryptedFile;
        try
        {
            encryptedFile = encrypt(file); 
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("file encryption error: " + e.getClass().toString() + ":\n" + e.getMessage());
        }

        File filePath = new File(customerDir + type + ".enc");//document.getExtension());
        try 
        {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(encryptedFile); 
            fos.close();
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("file storage error: " + e.getClass().toString() + ":\n" + e.getMessage());
        }

        // save credentials in db
        documentRepository.save(document);
        return ResponseEntity.ok().body("file stored successfully");
    }


    // file download method
    public ResponseEntity<?> downloadDoc(Customer customer, DocumentType type)
    {
        CustomerDocument document  = documentRepository.findByCustomerAndType(customer, type);
        if(document == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("file doesn't exist");
            
        String path = allCustomerDir + "/" + customer.getId() + "/" + document.getType() + ".enc";

        //decrypt attempt
        try
        {
            byte[] encryptedFile = Files.readAllBytes(new File(path).toPath());
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
        catch(InvalidKeyException e)
        {        return ResponseEntity.badRequest().body(e.getClass().toString() + ":\n" + e.getMessage());    }
        catch(Exception e)
        {        return ResponseEntity.badRequest().body(e.getClass().toString() + ":\n" + e.getMessage());    }
    }

    public String deleteCustomerDir(Long id){
        try
        {
            org.aspectj.util.FileUtil.deleteContents(new File(allCustomerDir + File.separator + id.toString()));
            Files.deleteIfExists(Paths.get(allCustomerDir + File.separator + id.toString()));
            return "customer files deleted!";
        }
        catch(IOException e)
        {return e.getClass().toString() + ":\n" + e.getMessage();}
    }

    public static byte[] encrypt(MultipartFile data) throws Exception 
    {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data.getBytes());
    }

    public static byte[] decrypt(byte[] data) throws Exception 
    {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public String scanImage(MultipartFile file)
    {
        Tesseract scanner = new Tesseract();
        try {
            File image = new File("C:\\Users\\DESKTOP-JIHEN\\Downloads\\output.png");
            file.transferTo(image);
            scanner.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            scanner.setLanguage("ara");
            String text = scanner.doOCR(image);
            image.delete();
            return text;
        } catch (Exception e) {
            return e.getClass().toString() + ":\n" + e.getMessage();
        }
    }
}
