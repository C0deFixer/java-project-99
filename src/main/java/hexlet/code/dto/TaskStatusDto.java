package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskStatusDto {
    private Long id;
    private String name;
    private String slug;
    private Date createdAt;
}
