package com.tugrulaslan.service;

import com.tugrulaslan.dto.AccountDto;
import com.tugrulaslan.dto.TransferRequestDto;

public interface AccountService {
    AccountDto retrieveById(int id);
    AccountDto transfer(int sourceAccountId, TransferRequestDto transferRequestDto);
}
