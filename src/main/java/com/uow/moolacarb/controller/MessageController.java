package com.uow.moolacarb.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uow.moolacarb.model.GoogleLoginRequest;
import com.uow.moolacarb.model.Message;
import com.uow.moolacarb.service.MessageService;
import com.uow.moolacarb.DataTransferObject.MessageRequest;

import jakarta.persistence.EntityNotFoundException;

import com.mysql.cj.x.protobuf.MysqlxCrud.Update;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service){
        this.service = service;
    }
    // TODO remove DTO, it is essentially a Message(?)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Message req) {
        try {
            service.create(req);
            return ResponseEntity.status(201).build(); // 201 Created
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Internal error");
        }
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody Message u) {
        u.setMessageId(id);
        service.update(u);
    }

}