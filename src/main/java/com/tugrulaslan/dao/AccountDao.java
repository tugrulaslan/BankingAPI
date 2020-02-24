package com.tugrulaslan.dao;

import com.tugrulaslan.entity.AccountEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountDao {
    Optional<AccountEntity> findAccountById(int id);
    void updateAmount(int sourceAccountId, int targetAccountId, BigDecimal amount);
}
