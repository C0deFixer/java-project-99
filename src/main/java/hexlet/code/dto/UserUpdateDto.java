package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDto {

    @NotNull
    private JsonNullable<String> firstName;
    @NotNull
    private JsonNullable<String>  lastName;

}
