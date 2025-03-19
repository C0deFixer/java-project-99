package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class TaskStatusesControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusMapper mapper;

    @Autowired
    private Faker faker;


    private MockMvc mockMvc;

    private TaskStatus createTaskStatus() {
        TaskStatus taskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(taskStatus);
        return taskStatus;
    }

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Test show")
    void testIndex() throws Exception {
        TaskStatus taskStatusTest1 = createTaskStatus();
        TaskStatus taskStatusTest2 = createTaskStatus();

        var request = get("/api/task_statuses");
        //.with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<TaskStatusDto> taskStatusDtos = om.readValue(body, new TypeReference<List<TaskStatusDto>>() {
        });
        List<TaskStatus> taskStatuses = taskStatusDtos.stream()
                .map(mapper::map).toList();
        assertThat(taskStatuses).containsExactlyInAnyOrderElementsOf(List.of(taskStatusTest1, taskStatusTest2));
    }

    @Test
    @DisplayName("Test index")
    void testShow() throws Exception {
        TaskStatus taskStatusTest1 = createTaskStatus();
        var request = get("/api/task_statuses/" + taskStatusTest1.getId())
                .with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("id").isEqualTo(taskStatusTest1.getId()),
                v -> v.node("name").isEqualTo(taskStatusTest1.getName()),
                v -> v.node("slug").isEqualTo(taskStatusTest1.getSlug()));
    }

    @Test
    @DisplayName("Test create")
    void testCreate() throws Exception {
        TaskStatus taskStatusTest = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var request = post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(mapper.map(taskStatusTest)));
        var result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();

        String body = result.getResponse().getContentAsString();
        TaskStatusDto taskStatusDto = om.readValue(body, new TypeReference<TaskStatusDto>() {
        });

        TaskStatus taskStatus = taskStatusRepository.findById(taskStatusDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found by id " + taskStatusDto.getId()));
        assertThatJson(body).and(v -> v.node("slug").isEqualTo(taskStatusTest.getSlug()),
                v -> v.node("name").isEqualTo(taskStatusTest.getName()));

    }

    @Test
    void testUpdate() throws Exception {
        TaskStatus taskStatusTest = createTaskStatus();
        taskStatusTest.setSlug(faker.lorem().word());
        taskStatusTest.setName(faker.lorem().word());
        var request = put("/api/task_statuses/" + taskStatusTest.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(mapper.map(taskStatusTest)));
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(String.valueOf(taskStatusTest.getId())),
                v -> v.node("slug").isEqualTo(taskStatusTest.getSlug()),
                v -> v.node("name").isEqualTo(taskStatusTest.getName()));
    }

    @Test
    @PreAuthorize("hasRole('ADMIN')")
    void testDelete() throws Exception {
        TaskStatus taskStatusTest = createTaskStatus();
        var request = delete("/api/task_statuses/" + taskStatusTest.getId())
                .with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

        assertThat(taskStatusRepository.findById(taskStatusTest.getId())).isEmpty();
    }

    @Test
    @DisplayName("Test access fail unauthorized")
    void testFailUnAuthorizedAccess() throws Exception {
        TaskStatus taskStatusTest = createTaskStatus();
        var request = delete("/api/task_statuses/" + taskStatusTest.getId());
        //.with(jwt())
        //.contentType(MediaType.APPLICATION_JSON);
        var result = mockMvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
    }
}