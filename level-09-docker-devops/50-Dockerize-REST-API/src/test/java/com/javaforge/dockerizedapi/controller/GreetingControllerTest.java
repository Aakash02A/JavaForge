package com.javaforge.dockerizedapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GreetingController.class)
class GreetingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsDefaultGreeting() throws Exception {
        mockMvc.perform(get("/api/greeting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, world!"));
    }

    @Test
    void includesNameInGreeting() throws Exception {
        mockMvc.perform(get("/api/greeting").param("name", "Java Forge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, Java Forge!"));
    }
}
