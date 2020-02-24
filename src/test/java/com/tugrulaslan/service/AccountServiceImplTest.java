package com.tugrulaslan.service;

import com.tugrulaslan.dao.AccountDao;
import com.tugrulaslan.dto.AccountDto;
import com.tugrulaslan.dto.TransferRequestDto;
import com.tugrulaslan.entity.AccountEntity;
import com.tugrulaslan.exception.AccountNotFoundException;
import com.tugrulaslan.mapper.AccountMapperImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

    private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal(10);
    private static final int SOURCE_ACCOUNT_ID = 1;
    private static final int TARGET_ACCOUNT_ID = 2;
    private static final BigDecimal BALANCE = new BigDecimal(1000);

    @Mock
    private AccountDao accountDao;

    @Spy
    private AccountMapperImpl accountMapper;

    @Mock
    private AccountValidatorService accountValidatorService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Captor
    private ArgumentCaptor<Integer> sourceAccountIdArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> targetAccountIdArgumentCaptor;

    @Captor
    private ArgumentCaptor<BigDecimal> amountArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> accountIdArgumentCaptor;

    @Captor
    private ArgumentCaptor<TransferRequestDto> transferRequestArgumentCaptor;

    @Test
    public void shouldRetrieveAccountEntityById() {
        //given
        int id = SOURCE_ACCOUNT_ID;
        AccountEntity accountEntity = createAccountEntity(id);
        given(accountDao.findAccountById(id)).willReturn(Optional.of(accountEntity));

        //when
        AccountDto accountDto = accountService.retrieveById(id);

        //then
        assertThat(accountDto).isNotNull();
        assertThat(accountDto.getId()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionWhenAccountDoesNotExist() {
        //given
        int id = SOURCE_ACCOUNT_ID;
        given(accountDao.findAccountById(id)).willReturn(Optional.empty());

        //when
        Throwable throwable = catchThrowable(() -> accountService.retrieveById(id));

        //then
        String expectedException = String.format("Given account with id '%s' not found", id);
        assertThat(throwable).isNotNull();
        assertThat(throwable)
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage(expectedException);
    }

    @Test
    public void shouldTransferBalance() {
        //given
        int id = SOURCE_ACCOUNT_ID;
        TransferRequestDto transferRequest = givenTransferRequest();
        AccountEntity accountEntity = createAccountEntity(id);
        given(accountDao.findAccountById(id)).willReturn(Optional.of(accountEntity));

        //when
        AccountDto account = accountService.transfer(id, transferRequest);

        //then
        then(accountDao).should().updateAmount(sourceAccountIdArgumentCaptor.capture(),
                targetAccountIdArgumentCaptor.capture(),
                amountArgumentCaptor.capture());
        then(accountValidatorService).should().validate(accountIdArgumentCaptor.capture(),
                transferRequestArgumentCaptor.capture());
        assertAccount(account);
        assertUpdateAmountValues();
        assertValidatedValues(transferRequest);
    }

    private AccountEntity createAccountEntity(int id) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setBalance(BALANCE);
        return accountEntity;
    }

    private TransferRequestDto givenTransferRequest() {
        TransferRequestDto transferRequestDto = new TransferRequestDto();
        transferRequestDto.setTargetAccountId(TARGET_ACCOUNT_ID);
        transferRequestDto.setAmount(TRANSFER_AMOUNT);
        return transferRequestDto;
    }

    private void assertAccount(AccountDto account) {
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(SOURCE_ACCOUNT_ID);
        assertThat(account.getBalance()).isEqualTo(BALANCE);
    }

    private void assertUpdateAmountValues() {
        Integer capturedSourceAccountId = sourceAccountIdArgumentCaptor.getValue();
        Integer capturedTargetAccountId = targetAccountIdArgumentCaptor.getValue();
        BigDecimal capturedAmount = amountArgumentCaptor.getValue();
        assertThat(capturedSourceAccountId).isEqualTo(SOURCE_ACCOUNT_ID);
        assertThat(capturedTargetAccountId).isEqualTo(TARGET_ACCOUNT_ID);
        assertThat(capturedAmount).isEqualTo(TRANSFER_AMOUNT);
    }

    private void assertValidatedValues(TransferRequestDto transferRequest) {
        Integer capturedAccountId = accountIdArgumentCaptor.getValue();
        TransferRequestDto capturedTransferRequest = transferRequestArgumentCaptor.getValue();
        assertThat(capturedAccountId).isEqualTo(SOURCE_ACCOUNT_ID);
        assertThat(capturedTransferRequest).isNotNull();
        assertThat(capturedTransferRequest).isEqualTo(transferRequest);
    }
}