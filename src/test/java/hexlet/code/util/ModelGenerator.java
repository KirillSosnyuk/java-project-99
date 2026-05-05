package hexlet.code.util;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Getter
@Component
public class ModelGenerator {
    private Model<Task> taskModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Label> labelModel;
    private Model<User> userModel;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @PostConstruct
    private void init() {

        userModel = Instancio.of(User.class)
            .ignore(Select.field(User::getId))
            .supply(Select.field(User::getFirstName), () -> faker.funnyName().name())
            .supply(Select.field(User::getLastName), () -> faker.funnyName().name())
            .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
            .supply(Select.field(User::getPassword), () -> faker.internet().password(4, 16))
            .ignore(Select.field(User::getCreatedAt))
            .ignore(Select.field(User::getUpdatedAt)).toModel();

        labelModel = Instancio.of(Label.class)
            .ignore(Select.field(Label::getId))
            .supply(Select.field(Label::getName), () -> faker.funnyName().name())
            .ignore(Select.field(Label::getCreatedAt)).toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
            .ignore(Select.field(TaskStatus::getId))
            .supply(Select.field(TaskStatus::getName), () -> faker.funnyName().name())
            .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
            .ignore(Select.field(TaskStatus::getCreatedAt)).toModel();

        taskModel = Instancio.of(Task.class)
            .ignore(Select.field(Task::getId))
            .supply(Select.field(Task::getName), () -> faker.funnyName().name())
            .supply(Select.field(Task::getIndex), () -> faker.number().numberBetween(0, 1000))
            .supply(Select.field(Task::getDescription), () -> faker.lorem().sentence())
            .supply(Select.field(Task::getTaskStatus), () -> {
                var status = Instancio.of(taskStatusModel).create();
                taskStatusRepository.save(status);
                return status;
            })
            .supply(Select.field(Task::getAssignee), () -> {
                var user = Instancio.of(userModel).create();
                userRepository.save(user);
                return user;
            })
            .supply(Select.field(Task::getLabels), () -> {
                var label = Instancio.of(labelModel).create();
                labelRepository.save(label);
                return new HashSet<>(Set.of(label));
            })
            .ignore(Select.field(Task::getCreatedAt)).toModel();
    }
}
