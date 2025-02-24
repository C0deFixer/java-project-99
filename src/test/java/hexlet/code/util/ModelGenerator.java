package hexlet.code.util;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {
    @Autowired
    private Faker faker;
    //private Model<Post> postModel;
    private Model<User> userModel;

    private Model<UserCreateDto> userCreateDtoModel;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {

        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                //.ignore(Select.field(User::getPosts))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                //.supply(Select.field(User::getPassword), () -> faker.internet().password())
                .supply(Select.field(User::getPasswordDigest), () -> passwordEncoder.encode(faker.internet().password()))
                .toModel();

        userCreateDtoModel = Instancio.of(UserCreateDto.class)
                .supply(Select.field(UserCreateDto::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(UserCreateDto::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDto::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDto::getPassword), () -> faker.internet().password())
                .toModel();
    }
}
