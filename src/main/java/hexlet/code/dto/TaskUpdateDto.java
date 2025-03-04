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
    private JsonNullable<String> name;
    //@NotBlank
    private JsonNullable<String> description;
    @NotNull
    private JsonNullable<Long> taskStatusId;
    private JsonNullable<Long> assigneeId;
    private JsonNullable<List<Long>> labelsId;

}
