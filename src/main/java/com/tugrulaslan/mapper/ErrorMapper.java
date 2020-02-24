package com.tugrulaslan.mapper;

import com.tugrulaslan.dto.ErrorMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ErrorMapper {
    ErrorMapper INSTANCE = Mappers.getMapper( ErrorMapper.class );

    ErrorMessageDto toErrorMessageDto(Integer statusCode, String message);
}
