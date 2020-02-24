package com.tugrulaslan.mapper;

import com.tugrulaslan.dto.ErrorMessageDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorMapperTest {

    private final ErrorMapper errorMapper = ErrorMapper.INSTANCE;

    @Test
    public void shouldMapToErrorMessageDto(){
        //given
        int statusCode = 404;
        String message = "This is an exceptional test case message";

        //when
        ErrorMessageDto mappedDto = errorMapper.toErrorMessageDto(statusCode, message);

        //then
        assertThat(mappedDto).isNotNull();
        assertThat(mappedDto.getStatusCode()).isEqualTo(statusCode);
        assertThat(mappedDto.getMessage()).isEqualTo(message);
    }

    @Test
    public void shouldMapReturnNullWhenParametersAreNull(){
        //given
        Integer statusCode = null;
        String message = null;

        //when
        ErrorMessageDto mappedDto = errorMapper.toErrorMessageDto(statusCode, message);

        //then
        assertThat(mappedDto).isNull();
    }
}
