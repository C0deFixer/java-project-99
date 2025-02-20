package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Faker faker;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    private User user1, user2;

    public User createUser() {
        User testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        return testUser;
    }

    @BeforeEach
    public void setUp() {

        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()) //attach SecurityContext to MockMvc
                .build();

        //this.user1 = createUser();
    }

    @Test
    public void testShow() throws Exception {

        User testUser = createUser();
        //using  SecurityMockMvcRequestPostProcessors.user  for test with explicit UserDetails
        //var request = get("/api/users/" + testUser.getId()).with(user(testUser));
        var request = get("/api/users/" + testUser.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isEqualTo(om.writeValueAsString(mapper.map(testUser)));
    }

    @Test
    public void testIndex() throws Exception {
        User user1 = createUser();
        User user2 = createUser();
        List<User> users = List.of(user1, user2);
        var request = get("/api/users").with(user(user1));
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<UserDto> userDTOS = om.readValue(body, new TypeReference<>() {
        });
        List<User> usersActual = userDTOS.stream().map(mapper::map).toList();
        assertThat(usersActual).containsExactlyInAnyOrderElementsOf(users);

    }

    @Test
    @DisplayName("Test create User")
    public void testCreate() throws Exception {
        User user1 = createUser();
        UserCreateDto userDto = Instancio.of(modelGenerator.getUserCreateDtoModel()).create();
        var request = post("/api/users").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDto));
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();


        User actualUserRepo = Optional.ofNullable(userRepository.findByEmail(userDto.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Test User not foud by e-mail " + userDto.getEmail()));
        assertThat(actualUserRepo.getPassword()).isEqualTo(passwordEncoder.encode(userDto.getPassword()));
        assertThatJson(body).and(v -> v.node("id").isEqualTo(actualUserRepo.getId()),
                v -> v.node("userName").isEqualTo(userDto.getEmail()),
                v -> v.node("firstName").isEqualTo(userDto.getFirstName()),
                v -> v.node("lastName").isEqualTo(userDto.getLastName())

        );
    }

    @Test
    @DisplayName("Test update User")
    public void testUpdate() throws Exception {
        User user1 = createUser();
        User testUser = Instancio.of(modelGenerator.getUserModel()).create();
        UserDto userDto = mapper.map(testUser);
        var request = put("/api/users/" + user1.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDto));
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();


        User actualUserRepo = userRepository.findById(user1.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Test User not foud by id " + user1.getId()));
        assertThatJson(body).and(v -> v.node("id").isEqualTo(user1.getId()),
                v -> v.node("userName").isEqualTo(user1.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName())

        );
    }

    @Test
    @DisplayName("Test delete User")
    public void testDelete() throws Exception {
        User testUser = createUser();
        userRepository.save(testUser);
        var request = delete("/api/users/" + testUser.getId()).with(jwt());
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(userRepository.findById(testUser.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Test login")
    public void testLogin() throws Exception {
        User testUser = createUser();
        Map<String, String> userDtoMap = new HashMap<>();
        userDtoMap.put("username", testUser.getEmail());
        userDtoMap.put("password", faker.internet().password());
        testUser.setPasswordDigest(passwordEncoder.encode(userDtoMap.get("password")));
        userRepository.save(testUser);
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDtoMap));
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(body).asBase64Decoded().isNotEmpty();
        //assertThat(body).asBase64Decoded().toString();

    }

}
