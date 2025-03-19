package hexlet.code.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDto {

    @NotNull
    private JsonNullable<String> firstName;
    @NotNull
    private JsonNullable<String> lastName;
    @Size(min =3, max = 100)
    private JsonNullable<String> password;

}
