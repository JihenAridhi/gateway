package com.uib.gateway.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.User;
import com.uib.gateway.Enums.UserStatus;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Enums.Role;
import com.uib.gateway.Repositories.UserRepository;

import jakarta.validation.Valid;

@Service
public class UserService implements UserDetailsService{

    @Autowired 
    UserRepository userRepository;

    @Autowired
    DocumentService documentService;

    @Autowired
    ControlListService controlListService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private TOTPService totpService = new TOTPService();
    private EmailService emailService = new EmailService();

    //private SecretKey key;;
    //private TimeBasedOneTimePasswordGenerator totp;

    public User findUserById(Long id) 
    {return userRepository.findById(id).orElse(null);}

    public List<User> findAllUsers()
    {return userRepository.findAll();}

    public User findUserByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public ResponseEntity<?> addUser(@Valid User user, DocumentType type, MultipartFile file)
    {
        if(userRepository.existsByEmail(user.getEmail()))
            return ResponseEntity.badRequest().body("Email exists!!");

            emailService.sendMail(
                user.getEmail(), 
                "TOTP test", 
                "Your verification code is: " + 
                totpService.generateTOTP(user.getEmail()) + 
                "\n The code expires in 150 seconds.");

        try
        {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setFirstName(user.getFirstName().toUpperCase());
            if (user.getMidName() != null) 
                user.setMidName(user.getMidName().toUpperCase());
            user.setLastName(user.getLastName().toUpperCase());
            //user.setRole(Role.CUSTOMER);
            user.setStatus(UserStatus.INACTIVE);

            User u = userRepository.save(user);
            return  ResponseEntity.status(HttpStatus.CREATED).body(u+"\n" + documentService.uploadDoc(file, type, u).getBody());
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User save error: " + e.getClass().toString() + ":\n" + e.getMessage());
        }
    }

    

    public boolean comapreTOTP(String email, String code)
    {
        return totpService.verifyTOTP(email, code);
    }

    public ResponseEntity<?> verifyTOTP(String email, String code)
    {
        if(totpService.verifyTOTP(email, code))
        {
            User user = userRepository.findByEmail(email);
            user.setStatus(UserStatus.ACTIVE);
            return ResponseEntity.ok().body("verified");
        }
        return ResponseEntity.ok().body("not verified");
    }

    public ResponseEntity<?> deleteUser(Long id) 
    {
        if (!userRepository.existsById(id)) 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User deletion failed!");
        documentService.deleteUserDir(id);
        userRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("\nUser deletion succeeded!");
    }

    public ResponseEntity<?> addDocToUser(MultipartFile file, DocumentType type, Long userId){
        User u = userRepository.findById(userId).orElse(null);
        try
        {
            return documentService.uploadDoc(file, type, u);
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body(e.getClass().toString() + ":\n" + e.getMessage());
        }   
    }

    public String updateUserStatus(User user, UserStatus status) {
        if(user == null)
            return "User not found";
        user.setStatus(status);
        userRepository.save(user);
        return "user status: " + status;
    }

    public String controlListScreening(User u) {
        if(controlListService.nameMatch(u.getAliases()))
        {
            if(u.getStatus() == UserStatus.UNDER_REVIEW)
                u.setStatus(UserStatus.REJECTED);
            else
                u.setStatus(UserStatus.UNDER_REVIEW);
        }
        else
            u.setStatus(UserStatus.APPROVED);
        userRepository.save(u);
        return u.getStatus().toString();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public boolean usernameExisits(String username)
    {
        return userRepository.existsByUsername(username);
    }
}
