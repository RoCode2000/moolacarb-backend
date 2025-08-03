package com.uow.moolacarb.controller;

import com.uow.moolacarb.model.TestUser;
import com.uow.moolacarb.repository.TestUserJdbcRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class TestUserController {

    private final TestUserJdbcRepository repo;

    public TestUserController(TestUserJdbcRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<TestUser> getAll() {
        return repo.findAll();
    }

    @GetMapping("/search")
    public List<TestUser> searchByName(@RequestParam String name) {
        return repo.findByName(name);
    }

    @PostMapping
    public String create(@RequestParam String name) {
        int rows = repo.insert(name);
        return rows + " user(s) added.";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        int rows = repo.deleteById(id);
        return rows + " user(s) deleted.";
    }
}
