package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Setter
@Getter
public class TaskUpdateDTO {

    private JsonNullable<Integer> index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<String> content;

    @NotBlank
    private JsonNullable<String> status;

    @NotEmpty
    private JsonNullable<Set<Long>> taskLabelIds;
}
