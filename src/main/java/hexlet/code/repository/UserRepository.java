package hexlet.code.repository;

import hexlet.code.model.User;
import hexlet.code.repository.projection.UserSummaryProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<UserSummaryProjection> findAllByOrderByIdAsc();
}
