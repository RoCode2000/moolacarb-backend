package com.uow.moolacarb.service;

import com.uow.moolacarb.model.Faq;
import com.uow.moolacarb.repository.FaqJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqService {
    private final FaqJdbcRepository repo;

    public FaqService(FaqJdbcRepository repo) {
        this.repo = repo;
    }

    public List<Faq> listActive(String category) {
        return repo.findActive(category);
    }

    public void create(Faq f) {
        if (f.getStatus() == null || f.getStatus().isBlank()) f.setStatus("A");
        if (f.getCategory() == null || f.getCategory().isBlank()) f.setCategory("general");
        repo.insert(f);
    }

    public void update(Faq f) {
        repo.update(f);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
