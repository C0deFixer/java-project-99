package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private Integer index;
    private String name;
    private String description;
    private Long assigneeId;
    private Long taskStatusId;
    private LocalDateTime createdAt;

}
