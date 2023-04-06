package com.stepan.simple_spring_rest.repository;

import com.stepan.simple_spring_rest.Task;

import java.util.List;

public interface TasksRepository {
    List<Task> findAll();
}
