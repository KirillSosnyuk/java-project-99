package hexlet.code.repository.projection;

import java.time.LocalDate;

public interface UserSummaryProjection {

    Long getId();

    String getEmail();

    String getFirstName();

    String getLastName();

    LocalDate getCreatedAt();
}
