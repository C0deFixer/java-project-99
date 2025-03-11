package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDto {
    private JsonNullable<Integer> index;
    @Size(min = 1)
    private JsonNullable<String> title;
    //@NotBlank
    private JsonNullable<String> content;
    @NotNull
    private JsonNullable<String> status;
    private JsonNullable<Long> assignee_id;
    private JsonNullable<List<Long>> taskLabelIds;

}
