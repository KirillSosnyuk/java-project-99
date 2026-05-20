package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String SEED_EMAIL = "hexlet@example.com";

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    private final UserRepository userRepository;

    private final CustomUserDetailsService userService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initUserIfAbsent();

        initTaskStatus("Draft", "draft");
        initTaskStatus("To review", "to_review");
        initTaskStatus("To be fixed", "to_be_fixed");
        initTaskStatus("To publish", "to_publish");
        initTaskStatus("Published", "published");

        initLabel("bug");
        initLabel("feature");
    }

    private void initUserIfAbsent() {
        if (userRepository.findByEmail(SEED_EMAIL).isPresent()) {
            return;
        }
        try {
            var userData = new User();
            userData.setEmail(SEED_EMAIL);
            userData.setPassword("qwerty");
            userService.createUser(userData);
        } catch (DataIntegrityViolationException ignored) {
            // уже создан другим инстансом или предыдущим запуском
        }
    }

    public void initTaskStatus(String name, String slug) {
        if (taskStatusRepository.existsBySlug(slug)) {
            return;
        }
        try {
            var taskStatusData = new TaskStatus();
            taskStatusData.setName(name);
            taskStatusData.setSlug(slug);
            taskStatusRepository.save(taskStatusData);
        } catch (DataIntegrityViolationException ignored) {
            // slug уже есть в БД
        }
    }

    public void initLabel(String name) {
        if (labelRepository.existsByName(name)) {
            return;
        }
        try {
            var labelData = new Label();
            labelData.setName(name);
            labelRepository.save(labelData);
        } catch (DataIntegrityViolationException ignored) {
            // name уже есть в БД
        }
    }
}
