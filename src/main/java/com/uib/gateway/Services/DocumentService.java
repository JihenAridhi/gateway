package com.uib.gateway.Services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.asprise.ocr.Ocr;
import com.uib.gateway.Entities.Document;
import com.uib.gateway.Entities.User;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Enums.Role;
import com.uib.gateway.Repositories.DocumentRepository;
import com.uib.gateway.Repositories.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import net.sourceforge.tess4j.Tesseract;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository cr;

    @Value("${document.storage.path}")
    private String storageDir ;
    private String allCustomerDir, allMerchantDir, allInternDir;

    @PostConstruct
    public void init() {
        this.allCustomerDir = storageDir + File.separator + "Customers" + File.separator;
        this.allMerchantDir = storageDir + File.separator + "Merchants" + File.separator;
        this.allInternDir = storageDir + File.separator + "Internes" + File.separator;
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

    public String getFileExtension(String fileName)
    {
        String extension;
        if (fileName == null || fileName.isEmpty()) 
            return fileName;
        int lastDotIndex = fileName.lastIndexOf('.');
        extension = fileName.substring(lastDotIndex, fileName.length());
        return extension;
    }

    
    public ResponseEntity<?> uploadDoc(MultipartFile file, DocumentType type, User user)
    {
        String dir = roleBasedDir(user.getRole());
        String userDir = dir + File.separator + user.getId() + File.separator;

        try
        {
            // create directory if it doesn't exist
            if(!Files.exists(Paths.get(userDir)))
                Files.createDirectory(Paths.get(userDir));

            // delete document if it already exist
            if(documentRepository.existsByUserAndType(user, type))
            {
                Files.deleteIfExists(new File(userDir+type+getFileExtension(file.getOriginalFilename())).toPath());
                documentRepository.delete(documentRepository.findByUserAndType(user, type));
            }
       
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("file storage directory error: " + e.getClass().toString() + ":\n" + e.getMessage());
        }

        // check if file is attached
        if(file == null || file.isEmpty())
            return ResponseEntity.badRequest().body("no file was attached");

        Document document = new Document(
            null,
            user,
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

        File filePath = new File(userDir + type + ".enc");
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
    @Transactional
    public ResponseEntity<?> downloadDoc(User user, DocumentType type)
    {   
        Document document  = documentRepository.findByUserAndType(user, type);
        if(document == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("file doesn't exist");

        String dir = roleBasedDir(user.getRole());
        String path = dir + "/" + user.getId() + "/" + document.getType() + ".enc";

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

    public String deleteUserDir(Long id){
        String dir = "";//roleBasedDir();
        try
        {
            FileUtil.deleteContents(new File(dir + File.separator + id.toString()));
            Files.deleteIfExists(Paths.get(dir + File.separator + id.toString()));
            return "User files deleted!";
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

    public String roleBasedDir(Role role)
    {
        if(role==Role.CUSTOMER)
            return allCustomerDir;
        if (role==Role.MERCHANT) 
            return allMerchantDir;
        return allInternDir;
    }

    public String scanImage(MultipartFile multipartFile)
    {
        return "";
        /* Tesseract scanner = new Tesseract();
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
        } */
    }
}
