package com.stepan.simple_spring_rest.repository;

import com.stepan.simple_spring_rest.Task;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class TasksRepositoryImpl implements TasksRepository {

    private  final List<Task> tasks = new LinkedList<>(){{
        this.add(new Task("First Task"));
        this.add(new Task("Second Task"));
        this.add(new Task("thid Task"));
    }};

    @Override
    public List<Task> findAll() {
        return this.tasks;
    }
}
