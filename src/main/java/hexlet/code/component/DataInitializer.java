package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    }
}
