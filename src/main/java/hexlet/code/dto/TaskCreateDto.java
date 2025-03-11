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
    private Long assignee_id;
    @Size(min = 1)
    private String title;
    //@NotBlank
    private String content;
    @NotNull
    private String status;

    private List<Long> taskLabelIds;
}
