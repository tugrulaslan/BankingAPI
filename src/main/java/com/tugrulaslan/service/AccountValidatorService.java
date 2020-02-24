package com.tugrulaslan.service;

import com.tugrulaslan.dao.AccountDao;
import com.tugrulaslan.dto.TransferRequestDto;
import com.tugrulaslan.entity.AccountEntity;
import com.tugrulaslan.exception.AccountNotFoundException;
import com.tugrulaslan.exception.InsufficientAccountBalanceException;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountValidatorService {
    public static final int MINIMUM_BALANCE_AMOUNT = 0;
    private final AccountDao accountDao;

    public AccountValidatorService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void validate(int sourceAccountId, TransferRequestDto transferRequestDto) {
        Optional<AccountEntity> sourceAccount = checkAccount(sourceAccountId);
        checkAccount(transferRequestDto.getTargetAccountId());
        checkSourceAccountBalance(sourceAccount, transferRequestDto);
    }

    private Optional<AccountEntity> checkAccount(int accountId) {
        Optional<AccountEntity> sourceAccount = accountDao.findAccountById(accountId);
        checkAccountExistence(sourceAccount, accountId);
        return sourceAccount;
    }

    private void checkAccountExistence(Optional<AccountEntity> sourceAccount, int accountId) {
        if (!sourceAccount.isPresent()) {
            String exceptionMessage = String.format("Given account with id '%s' not found", accountId);
            throw new AccountNotFoundException(exceptionMessage);
        }
    }

    private void checkSourceAccountBalance(Optional<AccountEntity> sourceAccount, TransferRequestDto transferRequestDto) {
        AccountEntity accountEntity = sourceAccount.get();
        BigDecimal sourceAccountBalance = accountEntity.getBalance();
        BigDecimal requestedAmount = transferRequestDto.getAmount();
        BigDecimal minimumBalance = new BigDecimal(MINIMUM_BALANCE_AMOUNT);
        int accountId = accountEntity.getId();
        if (accountHasInsufficientFunds(sourceAccountBalance, requestedAmount, minimumBalance)) {
            String exceptionMessage = String.format("The Account '%s' has insufficient funds '%.02f' to deduce '%.02f'",
                    accountId,
                    sourceAccountBalance,
                    requestedAmount);
            throw new InsufficientAccountBalanceException(exceptionMessage);
        }
    }

    private boolean accountHasInsufficientFunds(BigDecimal sourceAccountBalance,
                                                BigDecimal requestedAmount,
                                                BigDecimal minimumBalance) {
        return sourceAccountBalance.subtract(requestedAmount).compareTo(minimumBalance) < MINIMUM_BALANCE_AMOUNT;
    }
}
