package com.tugrulaslan.mapper;

import com.tugrulaslan.dto.AccountDto;
import com.tugrulaslan.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDto toAccountDto(AccountEntity accountEntity);
}
