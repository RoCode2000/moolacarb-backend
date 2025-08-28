package com.uow.moolacarb.service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.model.Message;
import com.uow.moolacarb.repository.MessageJdbcRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MessageService {
    private final MessageJdbcRepository repo;

    public MessageService(MessageJdbcRepository repo){
        this.repo = repo;
    }

    public void create(Message m){
        // Basic validation (optional, or use @Valid with Bean Validation)
        if (m.getEmail() == null || m.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required");
        if (m.getMessage() == null || m.getMessage().isBlank())
            throw new IllegalArgumentException("Message is required");
        if (m.getCreatedAt() == null) m.setCreatedAt(LocalDateTime.now());
        repo.insert(m);
    }

    public void update(Message m) {
        repo.update(m);
    }

    public List<Message> getMessages(Integer limit) {
        return repo.getMessages(limit);
    }

    public void delete(Integer id) {
        if (repo.getMessageById(id) == null) {
        throw new NoSuchElementException("Message not found");
    }
        repo.delete(id);
    }

    public void updateReply(String id, String reply) {
    int rows = repo.updateReply(id, reply, "R");
    if (rows == 0) throw new NoSuchElementException("Message not found");
}
}