package com.stepan.simple_spring_rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksRestControllerTest { // модульные тесты

    @Mock
    TasksRepository tasksRepository;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    TasksRestController controller;

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity(){
        // given
        var tasks = List.of(new Task(UUID.randomUUID(), "First Task", false),
                new Task(UUID.randomUUID(), "Second Task", true));
        Mockito.doReturn(tasks).when(this.tasksRepository).findAll();
        // when
        var responseEntity = this.controller.handleGetAllTasks();

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());
    }

    @Test
    void handleCreatNewTask_PayloadIsValid_ReturnValidResponseEntity(){
        // given
        var  details = "Third Task";

        // when
        var responseEntity = this.controller.handleCreatNewTask(new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8085"), Locale.ENGLISH);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        if(responseEntity.getBody() instanceof Task task ){
            assertNotNull(task.id());
            assertEquals(details, task.details());
            assertFalse(task.completed());

            assertEquals(URI.create("http://localhost:8085/api/tasks/" + task.id()),
                    responseEntity.getHeaders().getLocation());
        // корестность вызова к сторонней системе
            verify(this.tasksRepository).save(task);
        } else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }

        // небыло никаких обращений к репозиторию
        verifyNoMoreInteractions(this.tasksRepository);
    }

    @Test
    void handleCreatNewTask_PayloadIsInvalid_ReturnValidResponseEntity(){
        // given
        var details = "  ";
        var local = Locale.US;
        var errorMessage = "Details is empty";

        doReturn(errorMessage).when(this.messageSource)
                .getMessage("tasks.create.details.errors.not_set", new Object[0], local);

        // when
        var responseEntity = this.controller.handleCreatNewTask(new NewTaskPayload( details),
                UriComponentsBuilder.fromUriString("http://localhost:8085"), local);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorsPresentation(List.of(errorMessage)), responseEntity.getBody());

        // проверка что у репозитория не вызывался метот save или любые методы
        verifyNoInteractions(tasksRepository);


    }

}