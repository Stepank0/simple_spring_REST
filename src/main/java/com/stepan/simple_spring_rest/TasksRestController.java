package com.stepan.simple_spring_rest.controller;

import com.stepan.simple_spring_rest.Task;
import com.stepan.simple_spring_rest.repository.TasksRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TasksRestController {

    private final TasksRepository repository;

    public TasksRestController(TasksRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Task>> handleGetAllTasks(){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.repository.findAll());

    }
}
