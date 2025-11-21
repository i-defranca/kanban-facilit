package dev.challenge.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import config.TestcontainersConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(TestcontainersConfig.class)
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseMvcTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected Faker faker() {
        return new Faker();
    }

    protected String jsonContent(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}
