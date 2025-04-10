package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.AppApplication;
import hexlet.code.dto.AuthRequest;
import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserDto;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = AppApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


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

    @Autowired
    private JwtDecoder jwtDecoder;

    private User testUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    public User createUser() {
        User testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        return testUser;
    }

    @BeforeEach
    public void setUp() {

        userRepository.deleteAll();

        testUser = createUser();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()) //attach SecurityContext to MockMvc
                .build();

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));


    }

    @Test
    public void testShow() throws Exception {

        //using  SecurityMockMvcRequestPostProcessors.user  for test with explicit UserDetails
        //var request = get("/api/users/" + testUser.getId()).with(user(testUser));
        var request = get("/api/users/" + testUser.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isEqualTo(om.writeValueAsString(mapper.map(testUser)));
    }

    @Test
    public void testIndex() throws Exception {
        User user1 = createUser();
        List<User> users = List.of(testUser, user1);
        var request = get("/api/users").with(token);
        var result = mockMvc.perform(request)
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
        UserCreateDto userDto = Instancio.of(modelGenerator.getUserCreateDtoModel()).create();
        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDto));
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();


        User actualUserRepo = userRepository.findByEmail(userDto.getEmail()).orElse(null);
        assertNotNull(actualUserRepo);
        //TODO fix bug encode return inconsistent hash every time
        //assertThat(actualUserRepo.getPassword()).isEqualTo(passwordEncoder.encode(userDto.getPassword()));
        assertThatJson(body).and(v -> v.node("id").isEqualTo(actualUserRepo.getId()),
                v -> v.node("email").isEqualTo(userDto.getEmail()),
                v -> v.node("firstName").isEqualTo(userDto.getFirstName()),
                v -> v.node("lastName").isEqualTo(userDto.getLastName())

        );
    }

    @Test
    @DisplayName("Test update User")
    public void testUpdate() throws Exception {
        User data = Instancio.of(modelGenerator.getUserModel()).create();
        UserDto userDto = mapper.map(data);
        var request = put("/api/users/" + testUser.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDto));
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();


        User actualUserRepo = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(actualUserRepo);
        assertThatJson(body).and(v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("email").isEqualTo(data.getEmail()),
                v -> v.node("firstName").isEqualTo(data.getFirstName()),
                v -> v.node("lastName").isEqualTo(data.getLastName()),
                v -> v.node("createdAt").isPresent()

        );
    }

    @Test
    @DisplayName("Test delete User")
    public void testDelete() throws Exception {
        var request = delete("/api/users/{id}", testUser.getId()).with(token);
        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(userRepository.findById(testUser.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Test login")
    public void testLogin() throws Exception {
        AuthRequest authRequest = new AuthRequest(testUser.getEmail(), faker.internet().password());
        testUser.setPasswordDigest(passwordEncoder.encode(authRequest.getPassword()));
        userRepository.save(testUser);
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest));
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String body = result.getResponse().getContentAsString();
        assertThat(jwtDecoder.decode(body).getClaims().get("sub")).isEqualTo(testUser.getEmail());

    }

    @Test
    @DisplayName("Test decline login")
    public void tesLoginDecline() throws Exception {
        var request = get("/api/users"); //jwt missed
        var result = mockMvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @DisplayName("Test decline update another user")
    public void tesUpadateDecline() throws Exception {
        User testUser1 = createUser();
        UserDto userDto1 = mapper.map(testUser1);
        testUser1.setEmail(faker.internet().emailAddress());
        var request = put("/api/users/" + testUser.getId()).with(user(testUser1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDto1));
        var result = mockMvc.perform(request).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @DisplayName("Test decline delete another user")
    public void tesDeleteDecline() throws Exception {
        User testUser2 = createUser();
        var request = delete("/api/users/" + testUser.getId()).with(user(testUser2));
        var result = mockMvc.perform(request).andExpect(status().isForbidden()).andReturn();
    }
}
