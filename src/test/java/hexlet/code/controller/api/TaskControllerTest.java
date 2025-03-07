package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDto;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.util.TaskTestDto;
import hexlet.code.util.TaskTestMapper;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private LabelRepository labelRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private TaskTestMapper testMapper;

    @Autowired
    private ObjectMapper om;

    private MockMvc mvc;

    private User testUser;

    private TaskStatus testTaskStatus;

    private List<Label> testLabels;

    private JwtRequestPostProcessor token;
    @Autowired
    private LabelMapper labelMapper;

    private Task createTask() {
        Task taskTest = Instancio.of(modelGenerator.getTaskModel()).create();
        taskTest.setAssignee(testUser);
        taskTest.setTaskStatus(testTaskStatus);
        taskTest.addLabels(testLabels);
        return taskRepository.save(taskTest);
    }

    /**
     * Applying test Data from Dto Object as argument to testTask Object through Dto mapping
     */
    private Task createTask(TaskTestDto taskTestDto) {
        Task taskTest = Instancio.of(modelGenerator.getTaskModel()).create();
        taskTest.setAssignee(testUser);
        taskTest.setTaskStatus(testTaskStatus);
        taskTest.addLabels(testLabels);
        testMapper.apply(taskTestDto, taskTest);
        return taskRepository.save(taskTest);
    }

    private Label createLabel() {
        Label labelTest = Instancio.of(modelGenerator.getLabelModel()).create();
        return labelRepository.save(labelTest);
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();

        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        Label testLabel1 = createLabel();
        Label testLabel2 = createLabel();
        testLabels = List.of(testLabel1, testLabel2);
       /* testLabels = IntStream.of(3).boxed().map(x -> createLabel())
                .peek(System.out::println) //TODO replace to logger
                .toList();;*/
    }

    @Test
    @DisplayName("Test Task index")
    public void testIndex() throws Exception {
        List<Task> taskList = IntStream.of(5).boxed().map(x -> createTask())
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

    @ParameterizedTest(name = "Test Task Show {index} - {0} case")
    @ValueSource(strings = {"{\"assignee\": null}", "{\"labels\":[]}", "{\"assignee\":}, \"labels\":}"})
    //@ValueSource(strings  = {"{\"assignee\":null}"})
    //@ValueSource(strings  = {"{\"labels\":[]}"})
    public void testShow(String argDto) throws Exception {
        om.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        //Applying test argument to testTask Object through Dto mapping
        Task testTask = createTask(om.readValue(argDto, new TypeReference<TaskTestDto>() {
        }));

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

    @ParameterizedTest(name = "Test Task Create {index} - {0} case")
    @ValueSource(strings = {"{\"assignee\": null}", "{\"labels\":[]}", "{}"})
    public void testCreate(String argDto) throws Exception {

        TaskTestDto testTaskDto = om.readValue(argDto, new TypeReference<TaskTestDto>() {
        });

        Task testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);
        testTask.addLabels(testLabels);
        testMapper.apply(testTaskDto, testTask);
        String requestBody = om.writeValueAsString(mapper.map(testTask));
        var request = post("/api/tasks")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .with(token)
                .content(requestBody);

        List<Long> testLabelsId = testTask.getLabels().stream().map(Label::getId).toList();

        var responce = mvc.perform(request).andExpect(status().isCreated()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("index").isEqualTo(testTask.getIndex()),
                        v -> v.node("name").isEqualTo(testTask.getName()),
                        v -> v.node("description").isEqualTo(testTask.getDescription()),
                        v -> v.node("labels").isArray().containsExactlyInAnyOrderElementsOf(testLabelsId),
                        v -> v.node("taskStatusId").isEqualTo(testTask.getTaskStatus().getId()))
                .and(v -> {
                    if (testTask.getAssignee() == null) {
                        v.node("assigneeId").isAbsent();
                    } else {
                        v.node("assigneeId").isEqualTo(testTask.getAssignee().getId());
                    }
                });
    }


    @ParameterizedTest(name = "Test Task Show {index} - {0} ")
    @ValueSource(strings = {"", ""})
    public void testUpdate(String testModelBody) throws Exception {
        Task testTask = createTask();
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
        Task testTask = createTask();
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

    @Test
    @DisplayName("Fail delete user linked with Task")
    public void testfailDeleteUser() throws Exception {
        Task testTask = createTask();
        var request = delete("/api/users/" + testUser.getId())
                .with(jwt());
        var result = mvc.perform(request).andExpect(status().isBadRequest()).andReturn(); //Cascade
        //var result = mvc.perform(request).andReturn();
        assertThat(userRepository.findById(testUser.getId())).isPresent();
    }


}
