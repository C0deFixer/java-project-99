package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskStatusDto {
    private  Long id;
    private String slug;
    private String name;
    private LocalDateTime createdAt;

}
