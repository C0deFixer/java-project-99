package hexlet.code.dto;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskParamsDto {
    private String titleCont;
    private Long assigneeId;
    private String status; //slug
    private Long labelId;
}
