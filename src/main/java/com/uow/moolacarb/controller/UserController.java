package com.uow.moolacarb.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uow.moolacarb.model.GoogleLoginRequest;
import com.uow.moolacarb.model.User;
import com.uow.moolacarb.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @PostMapping("/google-login")
    public ResponseEntity<User> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            // System.out.println("Google login request: " + request);
                User user = new User();
                user.setUserId(UUID.randomUUID().toString());
                user.setEmail(request.getEmail());
                user.setFirstName(request.getGivenName());
                user.setLastName(request.getFamilyName());
                user.setFirebaseId(request.getFirebaseId());
                // user.setUsername(request.getName());
                // user.setFirebaseId(request.getIdToken()); // this is too many characters apparently 
                user.setCreatedDate(LocalDateTime.now());
                user.setLoginMethod("G");
            if( service.findByEmailAndLoginType(request.getEmail(), "G") == null){
                // System.out.println("Google login request internal: " + user.getFirebaseId());
                service.create(user);
                return ResponseEntity.ok(user);
            } else return ResponseEntity.ok(service.findByEmailAndLoginType(request.getEmail(), "G"));
            

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Map<String, String> request) {
        try {
                System.out.println("Login request: " + request);
                User user = new User();
                user.setUserId(UUID.randomUUID().toString());
                user.setEmail(request.get("email"));
                user.setFirstName(request.get("name"));
                user.setFirebaseId(request.get("firebaseId"));
                String hashedPassword = hashPassword(request.get("password"));
                user.setPasswordHash(hashedPassword);
                // user.setUsername(request.getName());
                // user.setFirebaseId(request.getIdToken()); // this is too many characters apparently 
                user.setCreatedDate(LocalDateTime.now());
                user.setLoginMethod("S");
            if(service.findByEmailAndLoginType(request.get("email"), "S") == null){
                service.create(user);
                return ResponseEntity.ok(user);
            } else 
            return ResponseEntity.ok(service.findByEmailAndLoginType(request.get("email"), "S"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody User u) {
        u.setUserId(id);
        service.update(u);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

}
