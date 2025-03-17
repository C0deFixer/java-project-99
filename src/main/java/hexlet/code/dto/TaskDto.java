package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private Integer index;
    private Date createdAt;
    private Long assignee_id;
    private String title;
    private String content;
    private String status;
    private List<Long> taskLabelIds;
}
