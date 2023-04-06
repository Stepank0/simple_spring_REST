package com.stepan.simple_spring_rest;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@RestController
@RequestMapping("api/tasks")
public class TasksRestController {

    private final TasksRepository repository;

    private final MessageSource messageSource;

    public TasksRestController(TasksRepository repository,
                               MessageSource messageSource) {
        this.repository = repository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<Task>> handleGetAllTasks() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.repository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> handleCreatNewTask(
            @RequestBody NewTaskPayload payload,
            UriComponentsBuilder uriComponentsBuilder,
            Locale locale) {
        if(payload.details()==null || payload.details().isBlank()){
            final var message = this.messageSource
                    .getMessage("tasks.create.details.errors.not_set",
                            new Object[0], locale);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorsPresentation(List.of(message)));
        } else {
            var task = new Task(payload.details());
            this.repository.save(task);
            return ResponseEntity.created(uriComponentsBuilder
                            .path("/api/tasks/{taskId}")
                            .build(Map.of("taskId", task.id())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(task);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Optional<Task>> handleFindTusk(@PathVariable("id") UUID id){
        return ResponseEntity.ok(this.repository.findById(id));
    }
}
