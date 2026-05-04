package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;

import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    private final LabelRepository labelRepository;

    @Autowired
    private final CustomUserDetailsService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        var userData = new User();
        userData.setEmail(email);
        userData.setPassword("qwerty");
        userService.createUser(userData);

        initTaskStatus("Draft", "draft");
        initTaskStatus("To review", "to_review");
        initTaskStatus("To be fixed", "to_be_fixed");
        initTaskStatus("To publish", "to_publish");
        initTaskStatus("Published", "published");

        initLabel("bug");
        initLabel("feature");
    }

    public void initTaskStatus(String name, String slug) {
        var taskStatusData = new TaskStatus();
        taskStatusData.setName(name);
        taskStatusData.setSlug(slug);
        taskStatusRepository.save(taskStatusData);
    }

    public void initLabel(String name) {
        var labelData = new Label();
        labelData.setName(name);
        labelRepository.save(labelData);
    }
}
