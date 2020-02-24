package com.tugrulaslan.service;

import com.tugrulaslan.dao.AccountDao;
import com.tugrulaslan.dto.AccountDto;
import com.tugrulaslan.dto.TransferRequestDto;
import com.tugrulaslan.entity.AccountEntity;
import com.tugrulaslan.exception.AccountNotFoundException;
import com.tugrulaslan.mapper.AccountMapper;

import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private final AccountDao accountDao;
    private final AccountMapper accountMapper;
    private final AccountValidatorService accountValidatorService;

    public AccountServiceImpl(AccountDao accountDao,
                              AccountMapper accountMapper,
                              AccountValidatorService accountValidatorService) {
        this.accountDao = accountDao;
        this.accountMapper = accountMapper;
        this.accountValidatorService = accountValidatorService;
    }

    @Override
    public AccountDto retrieveById(int id) {
        Optional<AccountEntity> account = accountDao.findAccountById(id);
        if (!account.isPresent()) {
            String exceptionMessage = String.format("Given account with id '%s' not found", id);
            throw new AccountNotFoundException(exceptionMessage);
        }
        return accountMapper.toAccountDto(account.get());
    }

    @Override
    public AccountDto transfer(int sourceAccountId, TransferRequestDto transferRequestDto) {
        accountValidatorService.validate(sourceAccountId, transferRequestDto);
        accountDao.updateAmount(sourceAccountId, transferRequestDto.getTargetAccountId(), transferRequestDto.getAmount());
        return retrieveById(sourceAccountId);
    }
}
