package ru.vasilev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vasilev.dao.PersonDAO;

@Controller
@RequestMapping("/test-batch")
public class BatchController {

    private final PersonDAO personDAO;

    @Autowired
    public BatchController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @GetMapping
    public String testBatch() {
        return "batch/index";
    }

    @GetMapping("/without")
    public String testBatchWithout() {
        personDAO.testMultipleUpdates();
        return "redirect:/people";
    }

    @GetMapping("/with")
    public String testBatchWith() {
        personDAO.testBatchUpdate();
        return "redirect:/people";
    }
}
