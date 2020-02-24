package com.tugrulaslan.mapper;

import com.tugrulaslan.dto.AccountDto;
import com.tugrulaslan.entity.AccountEntity;
import com.tugrulaslan.entity.CustomerEntity;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountMapperTest {

    private final AccountMapper accountMapper = AccountMapper.INSTANCE;

    @Test
    public void shouldMapToAccountDto() {
        //given
        CustomerEntity customerEntity = givenCustomerEntity();
        AccountEntity accountEntity = givenAccountEntity();

        //when
        AccountDto mappedAccount = accountMapper.toAccountDto(accountEntity);

        //then
        assertThat(mappedAccount).isNotNull();
        assertThat(mappedAccount.getId()).isEqualTo(accountEntity.getId());
        assertThat(mappedAccount.getBalance()).isEqualTo(accountEntity.getBalance());
        assertThat(mappedAccount.getCustomer().getId()).isEqualTo(customerEntity.getId());
        assertThat(mappedAccount.getCustomer().getName()).isEqualTo(customerEntity.getName());
    }

    @Test
    public void shouldReturnNullWhenAccountEntityIsNull() {
        //given-when
        AccountDto accountDto = accountMapper.toAccountDto(null);

        //then
        assertThat(accountDto).isNull();
    }

    private AccountEntity givenAccountEntity() {
        CustomerEntity customer = givenCustomerEntity();
        AccountEntity account = new AccountEntity();
        account.setId(1);
        account.setBalance(new BigDecimal(1251.45));
        account.setCustomer(customer);
        return account;
    }

    private CustomerEntity givenCustomerEntity() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1);
        customer.setName("tugrul aslan");
        return customer;
    }
}