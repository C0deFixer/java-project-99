package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserUpdateDto;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder encoder;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "userName")
    public abstract User map(UserDto model);

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User map(UserCreateDto userCreateDTO);

    @Mapping(target = "userName", source = "email")
    //@Mapping(target = "password", ignore = true)
    public abstract UserDto map(User userModel);

    public abstract void update(UserUpdateDto userDto, @MappingTarget User userModel);

    @BeforeMapping
    public void encryptPassword(UserCreateDto data) {
        var password = data.getPassword();
        data.setPassword(encoder.encode(password));
    }

}
