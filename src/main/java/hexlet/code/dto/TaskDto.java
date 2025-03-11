package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private Integer index;
    private LocalDateTime createdAt;
    private Long assignee_id;
    private String title;
    private String content;
    private Long status;
    private List<Long> taskLabelIds;
}
