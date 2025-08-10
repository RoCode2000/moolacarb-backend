package com.uow.moolacarb.controller;

import com.uow.moolacarb.model.Faq;
import com.uow.moolacarb.service.FaqService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // restrict to your React origin in prod
@RestController
@RequestMapping("/api/faqs")
public class FaqController {
    private final FaqService service;

    public FaqController(FaqService service) {
        this.service = service;
    }

    @GetMapping
    public List<Faq> getFaqs(@RequestParam(required = false) String category) {
        return service.listActive(category);
    }

    @PostMapping
    public void create(@RequestBody Faq f) { service.create(f); }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Faq f) {
        f.setFaqId(id);
        service.update(f);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
