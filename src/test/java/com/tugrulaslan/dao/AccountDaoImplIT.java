package com.tugrulaslan.dao;

import com.tugrulaslan.entity.AccountEntity;
import com.tugrulaslan.rule.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountDaoImplIT {

    private AccountDao accountDao;

    @Before
    public void setUp() {
        accountDao = new AccountDaoImpl();
    }

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    @Test
    public void shouldRetrieveAccountById() {
        //given
        int id = 1;

        //when
        Optional<AccountEntity> account = accountDao.findAccountById(id);

        //then
        assertThat(account).isPresent();
        AccountEntity accountEntity = account.get();
        assertThat(accountEntity.getId()).isEqualTo(id);
        assertThat(accountEntity.getBalance()).isEqualByComparingTo(new BigDecimal(2500));
        assertThat(accountEntity.getCustomer().getId()).isEqualTo(1);
        assertThat(accountEntity.getCustomer().getName()).isEqualTo("tugrul aslan");
    }

    @Test
    public void shouldUpdateAmount() {
        //given
        int sourceAccountId = 1;
        int targetAccountId = 2;
        BigDecimal amountToBeDeduced = new BigDecimal(500);

        //when
        accountDao.updateAmount(sourceAccountId, targetAccountId, amountToBeDeduced);

        //then
        AccountEntity sourceAccount = accountDao.findAccountById(sourceAccountId).get();
        AccountEntity targetAccount = accountDao.findAccountById(targetAccountId).get();
        assertThat(sourceAccount).isNotNull();
        assertThat(targetAccount).isNotNull();
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo(new BigDecimal(2000.00));
        assertThat(targetAccount.getBalance()).isEqualByComparingTo(new BigDecimal(5000.00));
    }
}