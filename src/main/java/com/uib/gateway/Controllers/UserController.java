package com.uib.gateway.Controllers;

import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Document;
import com.uib.gateway.Entities.User;
import com.uib.gateway.Enums.DocumentType;
import com.uib.gateway.Enums.Role;
import com.uib.gateway.Enums.UserStatus;
import com.uib.gateway.Services.JwtService;
import com.uib.gateway.Services.UserService;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

        @Autowired JwtService jwtService;

    // DTO class for login
    @Data @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // Resonse DTO
    @Data @AllArgsConstructor
    public static class ResponseDTO {
        private String message;
        private String userId;
        private String username;
        private String fullname;
        private String phone_nbr;
        private String email;
        private List<Document> documents;
        private Collection<? extends GrantedAuthority> authorities;
        //private Role role;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findUserByUsername(loginRequest.username);

            if (user!=null) {
                return ResponseEntity.ok().body(jwtService.generateToken(user)/* new ResponseDTO(
                        "Login successful",
                        user.getId().toString(),
                        user.getUsername(),
                        user.getFullname(),
                        user.getPhone(), //user.getRoles(),
                        user.getEmail(),
                        user.getDocuments(),
                        user.getAuthorities()
                ) */);
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during login");
        }
    }
    
    @GetMapping("findAll")
    public List<User> findAllUsers()
    {return userService.findAllUsers();}
    
    
    @PostMapping("register")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@ModelAttribute User user, @RequestParam MultipartFile file, @RequestParam DocumentType type)
    {
        return userService.addUser(user, type, file);
    }

    @GetMapping("{username}/exists")
    public boolean usernameExists(@PathVariable String username) {
        return userService.usernameExisits(username);
    }

    @GetMapping("{email}/testTOTP/{totp}")
    public boolean emailTOTP(@PathVariable String email, @PathVariable String totp) {
        return userService.comapreTOTP(email, totp);
    }

    @PostMapping("{userId}/addDoc")
    public ResponseEntity<?> addDoc(@PathVariable Long userId, @RequestParam MultipartFile file, @RequestParam DocumentType type){        
        return userService.addDocToUser(file, type, userId);
    }
    

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id)
    {return userService.deleteUser(id);}

    @PutMapping("/{id}/updateStatus")
    public String verifyUser(@PathVariable Long id, @RequestParam UserStatus status) {
        User c = userService.findUserById(id);
        return userService.updateUserStatus(c, status);
    }

    @PutMapping("{id}/screening")
    public String screening(@PathVariable Long id) {
        User c = userService.findUserById(id);
        return userService.controlListScreening(c);
    }



    @GetMapping("/jwt/test")
    public String getMethodName(@RequestParam String token) {
        //return jwtService.generateToken(userService.findUserByUsername("admin"));
        return jwtService.extractPayload(token);
    }
    
}
