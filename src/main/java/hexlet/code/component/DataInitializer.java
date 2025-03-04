package hexlet.code.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IOUtils ioUtils;
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

        //initiateData( "/data/taskStatuses.json", taskStatusRepository);
        initiateData( "/data/taskStatuses.json", taskStatusRepository, new TypeReference<List<TaskStatus>>(){});
        //initiateData("/data/labels.json", labelRepository);
        initiateData("/data/labels.json", labelRepository, new TypeReference<List<Label>>(){});
    }

    private <T> void initiateData(String path, JpaRepository repository, TypeReference<List<T>> tr) throws IOException {
        /*List<Map<String, Object>> dataList = objectMapper
                .readValue(ioUtils.readFileContent(path),
                        new TypeReference<List<Map<String, Object>>>() {});*/
        String jsonListOfObjects = ioUtils.readFileContent(path);
        List<T> dataList = objectMapper.readValue(jsonListOfObjects, tr);

        repository.saveAll(dataList);
    }


}
