package hexlet.code.util;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
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

    private Model<TaskStatus> taskStatusModel;

    private Model<Task> taskModel;

    private Model<Label> labelModel;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {

        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getTaskList))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
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

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getTasks))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(Select.field(TaskStatus::getName), () -> faker.job().position())
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getTaskStatus))
                .ignore(Select.field(Task::getLabels))
                .ignore(Select.field(Task::getCreatedAt))
                .supply(Select.field(Task::getName), () -> faker.name().title())
                .supply(Select.field(Task::getDescription), () -> faker.text().text(50))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getTasks))
                .supply(Select.field(Label::getName), () -> faker.name().title())
                .toModel();

    }
}
