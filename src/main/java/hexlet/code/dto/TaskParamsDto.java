package hexlet.code.dto;

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
