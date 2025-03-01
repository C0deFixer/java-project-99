package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private ObjectMapper om;

    private MockMvc mvc;

    private User testUser;

    private TaskStatus testTaskStatus;

    private JwtRequestPostProcessor token;

    private Task createTask(User user, TaskStatus taskStatus) {
        Task taskTest = Instancio.of(modelGenerator.getTaskModel()).create();
        taskTest.setAssignee(user);
        taskTest.setTaskStatus(taskStatus);
        return taskRepository.save(taskTest);
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    @DisplayName("Test Task index")
    public void testIndex() throws Exception {
        List<Task> taskList = IntStream.of(5).boxed().map(x -> createTask(testUser, testTaskStatus))
                .peek(System.out::println)
                .toList();
        var request = get("/api/tasks")
                .with(token);

        var responce = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<TaskDto> dtoList = om.readValue(body, new TypeReference<List<TaskDto>>() {
        });
        List<Task> taskListTest = dtoList.stream().map(mapper::map).toList();
        assertThat(taskListTest).containsExactlyInAnyOrderElementsOf(taskList);

    }

    @Test
    @DisplayName("Test Task Show")
    public void testShow() throws Exception {
        Task testTask = createTask(testUser, testTaskStatus);
        var request = get("/api/tasks/" + testTask.getId())
                .with(token);
        var responce = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("name").isEqualTo(testTask.getName()),
                v -> v.node("description").isEqualTo(testTask.getDescription()),
                v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("taskStatusId").isEqualTo(testTask.getTaskStatus().getId()));
    }

    @Test
    @DisplayName("Test Task Create")
    public void testCreate() throws Exception {
        Task testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);
        var request = post("/api/tasks")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .with(token)
                .content(om.writeValueAsString(mapper.map(testTask)));

        var responce = mvc.perform(request).andExpect(status().isCreated()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("name").isEqualTo(testTask.getName()),
                v -> v.node("description").isEqualTo(testTask.getDescription()),
                v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("taskStatusId").isEqualTo(testTask.getTaskStatus().getId()));
    }

    @Test
    @DisplayName("Test Task Update")
    public void testUpdate() throws Exception {
        Task testTask = createTask(testUser, testTaskStatus);
        User testUser1 = Instancio.of(modelGenerator.getUserModel()).create();
        TaskStatus testTaskStatus1 = Instancio.of(modelGenerator.getTaskStatusModel()).create();

        testTask.setIndex(faker.number().positive());
        testTask.setName(faker.name().title());
        testTask.setDescription(faker.text().text(50));
        testTask.setAssignee(userRepository.save(testUser1));
        testTask.setTaskStatus(taskStatusRepository.save(testTaskStatus1));

        TaskDto taskDto = mapper.map(testTask);
        String testBody = om.writeValueAsString(taskDto);
        var request = put("/api/tasks/" + testTask.getId())
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .with(token)
                .content(testBody);

        var responce = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("name").isEqualTo(testTask.getName()),
                v -> v.node("description").isEqualTo(testTask.getDescription()),
                v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("taskStatusId").isEqualTo(testTask.getTaskStatus().getId()));
    }

    @Test
    @DisplayName("Test Task Delete")
    public void testDelete() throws Exception {
        Task testTask = createTask(testUser, testTaskStatus);
        var request = delete("/api/tasks/" + testTask.getId())
                .with(token);
        var result = mvc.perform(request).andExpect(status().isNoContent()).andReturn();
        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }

    @Test
    @DisplayName("Fail access without token")
    public void testfailAccess() throws Exception {
        var request = delete("/api/tasks/"); //No token apply
        var result = mvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
    }


}
