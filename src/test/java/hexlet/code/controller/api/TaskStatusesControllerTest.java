package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskStatusMapper statusMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JwtRequestPostProcessor token;

    private User testUser;

    private TaskStatus testStatus;

    @BeforeEach
    public void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tasks_labels", "tasks", "labels", "task_statuses", "users");

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .apply(springSecurity()).build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        statusRepository.save(testStatus);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel()).create();

        var request = post("/api/task_statuses").with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isCreated());

        var status = statusRepository.findByName(data.getName());

        assertNotNull(status);
        assertThat(status.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        List<TaskStatusDTO> statusDTOS = om.readValue(body, new TypeReference<>() {
        });

        var actual = statusDTOS.stream().map(statusMapper::map).toList();
        var expected = statusRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testStatus.getId()).with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(v -> {
            v.node("id").isEqualTo(testStatus.getId());
            v.node("name").isEqualTo(testStatus.getName());
            v.node("createdAt").isEqualTo(testStatus.getCreatedAt().toString());
        });
    }

    @Test
    public void testUpdate() throws Exception {

        var data = new HashMap<>();
        data.put("slug", "checked_at");

        var request = put("/api/task_statuses/" + testStatus.getId())
            .with(token).contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());

        var status = statusRepository.findById(testStatus.getId()).orElseThrow();
        assertThat(status.getSlug()).isEqualTo(("checked_at"));
    }

    @Test
    public void testCreateInvalid() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        data.setName("");

        var request = post("/api/task_statuses").with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isBadRequest());

        var status = statusRepository.findByName(data.getName());
        assertNull(status);
    }

    @Test
    public void testUpdateInvalid() throws Exception {

        var data = new HashMap<>();
        data.put("name", "");

        var request = put("/api/task_statuses/" + testStatus.getId())
            .with(token).contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isBadRequest());

        var status = statusRepository.findById(testStatus.getId()).orElseThrow();
        assertThat(status.getName()).isNotEqualTo((""));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId()).with(token))
            .andExpect(status().isNoContent());

        assertThat(statusRepository.findById(testStatus.getId())).isEmpty();
    }

    @Test
    public void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/task_statuses/999999").with(jwt()))
            .andExpect(status().isNotFound());
    }
}
