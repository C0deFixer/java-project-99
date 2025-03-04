package hexlet.code.mapper;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
unmappedTargetPolicy =  ReportingPolicy.IGNORE)
public abstract class LabelMapper {
    public abstract Label map(LabelDto dto);
    public abstract LabelDto map(Label model);
    public abstract void update(LabelDto dto,@MappingTarget Label model);
}
