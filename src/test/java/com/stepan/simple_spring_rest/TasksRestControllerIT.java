package com.stepan.simple_spring_rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false) //просмотр лога запроса и ответа
class TasksRestControllerIT { // интеграционные тесты

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TasksRepositoryImpl tasksRepository;

    @AfterEach
    void tearDown() {
        this.tasksRepository.getTasks().clear();
    }

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/tasks");
        this.tasksRepository.getTasks()
                .addAll(List.of(new Task(UUID.fromString("2f03ff43-f1b8-421f-ab52-327e6c010f99"),
                                "First Task", false),
                        new Task(UUID.fromString("1045581b-f5b4-467a-a519-48a750753974"),
                                "Second Task", true)));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                {
                                    "id": "2f03ff43-f1b8-421f-ab52-327e6c010f99",
                                    "details": "First Task",
                                    "completed": false
                                },
                                {
                                    "id": "1045581b-f5b4-467a-a519-48a750753974",
                                    "details": "Second Task",
                                    "completed": true
                                }
                                ]
                                """)
                );
    }

    @Test
    void handleCreatNewTask_PayloadIsValid_ReturnValidResponseEntity() throws Exception {
        // given
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details": "Third Tasks"
                        }
                        """);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().exists(HttpHeaders.LOCATION),
                        content().json("""
                                {
                                    "details": "Third Tasks",
                                    "completed": false
                                }
                                """),
                        jsonPath("$.id").exists()
                );

        assertEquals(1, this.tasksRepository.getTasks().size());
        assertNotNull(this.tasksRepository.getTasks().get(0).id());
        assertEquals("Third Tasks", this.tasksRepository.getTasks().get(0).details());
        assertFalse(this.tasksRepository.getTasks().get(0).completed());
    }

    @Test
    void handleCreatNewTask_PayloadIsInvalid_ReturnValidResponseEntity() throws Exception {
        // given
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .content("""
                        {
                            "details": null
                        }
                        """);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "errors": ["Tasks details must be set"]
                                }
                                """, true)
                );

        assertTrue(this.tasksRepository.getTasks().isEmpty());

    }


}