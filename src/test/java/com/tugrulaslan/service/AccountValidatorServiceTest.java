package com.tugrulaslan.service;

import com.tugrulaslan.dao.AccountDao;
import com.tugrulaslan.dto.TransferRequestDto;
import com.tugrulaslan.entity.AccountEntity;
import com.tugrulaslan.exception.AccountNotFoundException;
import com.tugrulaslan.exception.InsufficientAccountBalanceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccountValidatorServiceTest {
    private static final int SOURCE_ACCOUNT_ID = 1;
    private static final int TARGET_ACCOUNT_ID = 2;
    private static final BigDecimal BALANCE = new BigDecimal(200);
    private static final String ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE = "Given account with id '%s' not found";

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountValidatorService accountValidatorService;

    @Test
    public void shouldValidate() {
        //given
        TransferRequestDto transferRequestDto = givenTransferRequest();
        givenMocks(BALANCE);

        //when - then
        accountValidatorService.validate(SOURCE_ACCOUNT_ID, transferRequestDto);
    }

    @Test
    public void shouldThrowExceptionWhenSourceAccountDoesNotExist() {
        //given
        TransferRequestDto transferRequestDto = givenTransferRequest();
        given(accountDao.findAccountById(SOURCE_ACCOUNT_ID)).willReturn(Optional.empty());

        //when
        Throwable throwable = catchThrowable(() -> accountValidatorService.validate(SOURCE_ACCOUNT_ID, transferRequestDto));

        //then
        String expectedException = String.format(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE, SOURCE_ACCOUNT_ID);
        assertAccountExceptionMessage(throwable, expectedException);
    }

    @Test
    public void shouldThrowExceptionWhenTargetAccountDoesNotExist() {
        //given
        TransferRequestDto transferRequestDto = givenTransferRequest();
        given(accountDao.findAccountById(SOURCE_ACCOUNT_ID)).willReturn(Optional.of(createAccountEntity(SOURCE_ACCOUNT_ID, BALANCE)));
        given(accountDao.findAccountById(TARGET_ACCOUNT_ID)).willReturn(Optional.empty());

        //when
        Throwable throwable = catchThrowable(() -> accountValidatorService.validate(SOURCE_ACCOUNT_ID, transferRequestDto));

        //then
        String expectedException = String.format(ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE, TARGET_ACCOUNT_ID);
        assertAccountExceptionMessage(throwable, expectedException);
    }

    @Test
    public void shouldThrowExceptionWhenAccountBalanceIsInsufficient() {
        //given
        BigDecimal balance = new BigDecimal(1);
        TransferRequestDto transferRequestDto = givenTransferRequest();
        givenMocks(balance);

        //when
        Throwable throwable = catchThrowable(() -> accountValidatorService.validate(SOURCE_ACCOUNT_ID, transferRequestDto));

        //then
        String expectedException = String.format("The Account '%s' has insufficient funds '%.02f' to deduce '%.02f'",
                SOURCE_ACCOUNT_ID,
                balance,
                transferRequestDto.getAmount());
        assertThat(throwable).isNotNull();
        assertThat(throwable)
                .isInstanceOf(InsufficientAccountBalanceException.class)
                .hasMessage(expectedException);
    }

    private void givenMocks(BigDecimal balance) {
        given(accountDao.findAccountById(SOURCE_ACCOUNT_ID)).willReturn(Optional.of(createAccountEntity(SOURCE_ACCOUNT_ID, balance)));
        given(accountDao.findAccountById(TARGET_ACCOUNT_ID)).willReturn(Optional.of(createAccountEntity(TARGET_ACCOUNT_ID, balance)));
    }

    private TransferRequestDto givenTransferRequest() {
        TransferRequestDto transferRequestDto = new TransferRequestDto();
        transferRequestDto.setTargetAccountId(TARGET_ACCOUNT_ID);
        transferRequestDto.setAmount(new BigDecimal(10));
        return transferRequestDto;
    }

    private AccountEntity createAccountEntity(int id, BigDecimal balance) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setBalance(balance);
        return accountEntity;
    }

    private void assertAccountExceptionMessage(Throwable throwable, String expectedException) {
        assertThat(throwable).isNotNull();
        assertThat(throwable)
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage(expectedException);
    }
}