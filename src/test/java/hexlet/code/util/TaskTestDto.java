package hexlet.code.util;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskTestDto {
    @JsonSetter(nulls = Nulls.SET)
    private JsonNullable<User> assignee;
    //@JsonSetter(contentNulls = Nulls.AS_EMPTY)
    private JsonNullable<Set<Label>> labels;
}
