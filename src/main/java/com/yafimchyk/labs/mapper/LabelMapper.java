package com.yafimchyk.labs.mapper;

import com.yafimchyk.labs.dto.request.LabelRequestDto;
import com.yafimchyk.labs.dto.response.LabelResponseDto;
import com.yafimchyk.labs.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    LabelResponseDto toDto(Label label);

    Label toEntity(LabelRequestDto labelRequestDto);

    void updateLabelFromDto(LabelRequestDto request, @MappingTarget Label label);
}
