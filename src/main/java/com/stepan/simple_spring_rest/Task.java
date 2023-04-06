package com.stepan.simple_spring_rest;

import java.util.UUID;

public record Task(UUID id, String details, boolean completed) {

    public Task(String details){
        this(UUID.randomUUID(), details, false);
    }
}
