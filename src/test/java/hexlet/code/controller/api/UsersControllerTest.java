package hexlet.code.controller.api;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

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

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

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
    private JdbcTemplate jdbcTemplate;

    private JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tasks_labels", "tasks", "labels", "task_statuses", "users");

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .apply(springSecurity()).build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel()).create();

        var request = post("/api/users").with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).orElse(null);

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/users").with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        List<UserDTO> userDTOS = om.readValue(body, new TypeReference<>() {
        });

        var actual = userDTOS.stream().map(userMapper::map).toList();
        var expected = userRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(v -> {
            v.node("id").isEqualTo(testUser.getId());
            v.node("email").isEqualTo(testUser.getEmail());
            v.node("firstName").isEqualTo(testUser.getFirstName());
            v.node("lastName").isEqualTo(testUser.getLastName());
            v.node("createdAt").isEqualTo(testUser.getCreatedAt().toString());
        });
    }

    @Test
    public void testUpdate() throws Exception {

        var data = new HashMap<>();
        data.put("firstName", "Mike");

        var request = put("/api/users/" + testUser.getId())
            .with(token).contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getFirstName()).isEqualTo(("Mike"));
    }

    @Test
    public void testUpdateForbidden() throws Exception {

        var altUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(altUser);
        var altToken = jwt().jwt(builder -> builder.subject(altUser.getEmail()));

        var data = new HashMap<>();
        data.put("firstName", "Mike");

        var request = put("/api/users/" + testUser.getId())
            .with(altToken).contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isForbidden());

        var user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getFirstName()).isNotEqualTo(("Mike"));
    }

    @Test
    public void testCreateInvalid() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel()).create();
        data.setEmail("fgvcscgs");

        var request = post("/api/users").with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isBadRequest());

        var user = userRepository.findByEmail(data.getEmail()).orElse(null);

        assertNull(user);
    }

    @Test
    public void testUpdateInvalid() throws Exception {

        var data = new HashMap<>();
        data.put("email", "fgvcscgs");

        var request = put("/api/users/" + testUser.getId())
            .with(token).contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isBadRequest());

        var user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getEmail()).isNotEqualTo(("fgvcscgs"));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(token))
            .andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    @Test
    public void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999999").with(jwt()))
            .andExpect(status().isNotFound());
    }
}
