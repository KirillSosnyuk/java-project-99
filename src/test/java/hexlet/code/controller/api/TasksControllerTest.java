package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
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
import java.util.Set;

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
public class TasksControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JwtRequestPostProcessor token;

    private User testUser;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tasks_labels", "tasks", "labels", "task_statuses", "users");

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .apply(springSecurity())
            .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        taskRepository.save(testTask);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskModel()).create();
        var map = taskMapper.map(data);

        var request = post("/api/tasks").with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(map));
        mockMvc.perform(request).andExpect(status().isCreated());

        var task = taskRepository.findByName(data.getName());

        assertNotNull(task);
        assertThat(task.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks").with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        List<TaskDTO> taskDTOS = om.readValue(body, new TypeReference<>() {
        });

        var actual = taskDTOS.stream().map(taskMapper::map).toList();
        var expected = taskRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        Set<Long> labelIds = taskMapper.map(testTask).getTaskLabelIds();
        var request = get("/api/tasks/" + testTask.getId()).with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(v -> {
            v.node("id").isEqualTo(testTask.getId());
            v.node("title").isEqualTo(testTask.getName());
            v.node("index").isEqualTo(testTask.getIndex());
            v.node("content").isEqualTo(testTask.getDescription());
            v.node("status").isEqualTo(testTask.getTaskStatus().getSlug());
            v.node("assignee_id").isEqualTo(testTask.getAssignee().getId());
            v.node("taskLabelIds").isEqualTo(labelIds);
            v.node("createdAt").isEqualTo(testTask.getCreatedAt().toString());
        });
    }

    @Test
    public void testUpdate() throws Exception {

        var data = new HashMap<>();
        data.put("title", "To do");

        var request = put("/api/tasks/" + testTask.getId())
            .with(token).contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(task.getName()).isEqualTo(("To do"));
    }

    @Test
    public void testCreateInvalid() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskModel()).create();
        data.setName("");

        var request = post("/api/tasks").with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isBadRequest());

        var task = taskRepository.findByName(data.getName());
        assertNull(task);
    }

    @Test
    public void testUpdateInvalid() throws Exception {

        var data = new HashMap<>();
        data.put("title", "");

        var request = put("/api/tasks/" + testTask.getId())
            .with(token).contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isBadRequest());

        var task = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(task.getName()).isNotEqualTo((""));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId()).with(token))
            .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }

    @Test
    public void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/999999").with(jwt()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testIndexWithStatusFilter() throws Exception {
        var slug = testTask.getTaskStatus().getSlug();

        var response = mockMvc.perform(get("/api/tasks").param("status", slug).with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getHeader("X-Total-Count")).isEqualTo("1");
    }
}
