package hexlet.code.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String email = "hexlet@example.com";
        if (userRepository.findByEmail(email) != null) {
            return;
        }
        User admin = new User();
        admin.setEmail(email);
        admin.setPasswordDigest(passwordEncoder.encode("qwerty"));
        admin.setFirstName("admin");
        admin.setLastName("admin");
        userRepository.save(admin);


        initiateTaskStatuses("/data/taskStatuses.json");
    }

    private void initiateTaskStatuses(String path) throws IOException {
        InputStream resource = new ClassPathResource(path).getInputStream();
        String taskStatuses;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
            taskStatuses = reader.lines()
                    .collect(Collectors.joining("\n"));
        }
        List<TaskStatus> taskStatusList = objectMapper.readValue(taskStatuses, new TypeReference<List<TaskStatus>>() {
        });
        taskStatusRepository.saveAll(taskStatusList);

    }
}
