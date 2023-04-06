package com.stepan.simple_spring_rest;

import com.stepan.simple_spring_rest.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TasksRepository {
    List<Task> findAll();

    void save(Task task);

    Optional<Task> findById(UUID id);
}
