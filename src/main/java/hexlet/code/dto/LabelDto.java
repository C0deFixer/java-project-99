package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LabelDto {
    private String name;
    private LocalDateTime createdAt;

}
