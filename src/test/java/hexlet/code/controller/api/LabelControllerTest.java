package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private LabelMapper mapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    private MockMvc mockMvc;

    private Label testLabel;


    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()) //attach SecurityContext to MockMvc
                .build();

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    @DisplayName("Test Labels show")
    public void testShow() throws Exception {
        Label testLabel1 = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel1);
        var request = get("/api/labels/" + testLabel.getId())
                .with(jwt());
        var responce = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel.getName()));

    }

    @Test
    @DisplayName("Test Labels index")
    public void testIndex() throws Exception {
        Label testLabel1 = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel1);
        var request = get("/api/labels")
                .with(jwt());
        var responce = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<LabelDto> listDto = om.readValue(body, new TypeReference<List<LabelDto>>() {
        });

        List<Label> labelsList = listDto.stream().map(mapper::map).toList();
        assertThat(labelsList).containsExactlyInAnyOrderElementsOf(List.of(testLabel, testLabel1));
    }

    @Test
    @DisplayName("Test Label create")
    public void testCreate() throws Exception {
        Label testLabel1 = Instancio.of(modelGenerator.getLabelModel()).create();
        var request = post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(mapper.map(testLabel1)));

        var responce = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        String body = responce.getResponse().getContentAsString();
        Label labelActual = labelRepository.findByName(testLabel1.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Label with name"
                        + testLabel1.getName() + " not found"));
        assertThatJson(body).and(v -> v.node("id").isEqualTo(labelActual.getId()),
                v -> v.node("name").isEqualTo(testLabel1.getName()));

    }

    @Test
    @DisplayName("Test Label update")
    public void testUpdate() throws Exception {
        Label testLabel1 = Instancio.of(modelGenerator.getLabelModel()).create();
        LabelDto dto = mapper.map(testLabel1);
        var request = put("/api/labels/" + testLabel.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var responce = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = responce.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel1.getName()));
        Label labelActual = labelRepository.findById(testLabel.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Label with id" + testLabel.getId() + " not found"));
        assertThat(labelActual.getName()).isEqualTo(testLabel1.getName());
    }

    @Test
    @DisplayName("Test Label delete")
    public void testDelete() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId())
                .with(jwt());
        var responce = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();
        assertThat(labelRepository.findById(testLabel.getId())).isEmpty();
    }


    @Test
    @DisplayName("Test Fail Label repetable name creating")
    public void testFailNameRepeate() throws Exception {
        LabelDto dto = mapper.map(testLabel);
        labelRepository.save(testLabel);
        var request = post("/api/labels")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        var responce = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
    }

}
