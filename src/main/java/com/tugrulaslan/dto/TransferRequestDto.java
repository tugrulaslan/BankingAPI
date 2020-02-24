package com.tugrulaslan.dto;

import java.math.BigDecimal;

public class TransferRequestDto {
    private int targetAccountId;
    private BigDecimal amount;

    public int getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(int targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    }
