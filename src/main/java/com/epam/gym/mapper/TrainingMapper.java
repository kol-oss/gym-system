package com.epam.gym.mapper;

import com.epam.gym.dto.CreateTrainingDto;
import com.epam.gym.dto.UpdateTrainingDto;
import com.epam.gym.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TrainingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "type", ignore = true)
    Training toEntity(CreateTrainingDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "type", ignore = true)
    void updateEntityFromDto(UpdateTrainingDto dto, @MappingTarget Training entity);
}
