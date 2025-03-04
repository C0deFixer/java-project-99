package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskCreateDto {
    private Integer index;
    @Size(min = 1)
    private String name;
    //@NotBlank
    private String description;
    @NotNull
    private Long taskStatusId;
    private Long assigneeId;
    private List<Long> labels;
}
