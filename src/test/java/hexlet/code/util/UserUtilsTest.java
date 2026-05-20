package hexlet.code.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest
public class UserUtilsTest {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tasks_labels", "tasks", "labels", "task_statuses", "users");
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetCurrentUserWhenAuthenticated() {
        var user = Instancio.of(modelGenerator.getUserModel()).create();
        userDetailsService.createUser(user);

        var authentication = UsernamePasswordAuthenticationToken.authenticated(
            user.getEmail(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var current = userUtils.getCurrentUser();

        assertThat(current).isNotNull();
        assertThat(current.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testGetCurrentUserWhenNotAuthenticated() {
        assertThat(userUtils.getCurrentUser()).isNull();
    }

    @Test
    public void testGetTestUser() {
        var user = new User();
        user.setEmail("hexlet@example.com");
        user.setPassword("qwerty");
        userDetailsService.createUser(user);

        var result = userUtils.getTestUser();

        assertThat(result.getEmail()).isEqualTo("hexlet@example.com");
    }

    @Test
    public void testGetTestUserWhenMissing() {
        assertThatThrownBy(() -> userUtils.getTestUser())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User doesn't exist");
    }
}
