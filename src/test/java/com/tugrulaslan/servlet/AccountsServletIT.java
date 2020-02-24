package com.tugrulaslan.servlet;


import com.tugrulaslan.dto.AccountDto;
import com.tugrulaslan.dto.TransferRequestDto;
import com.tugrulaslan.exception.AccountNotFoundException;
import com.tugrulaslan.exception.InsufficientAccountBalanceException;
import com.tugrulaslan.exception.InvalidPathParameterException;
import com.tugrulaslan.rule.DatabaseRule;
import com.tugrulaslan.server.TomcatServer;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountsServletIT {

    private static final String ACCOUNTS_URL = "http://localhost:8080/accounts/";
    private static final int SOURCE_ACCOUNT_ID = 1;
    private static final int TARGET_ACCOUNT_ID = 2;
    private static final BigDecimal BALANCE = new BigDecimal(2500);
    private static final TomcatServer tomcatServer = TomcatServer.INSTANCE;

    @BeforeClass
    public static void beforeClass() throws Exception {
        tomcatServer.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        tomcatServer.stop();
    }

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    @Test
    public void shouldGetAccountById() {
        //given-when
        AccountDto account = getAndAssertAccount(SOURCE_ACCOUNT_ID);

        //then
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(SOURCE_ACCOUNT_ID);
        assertThat(account.getBalance()).isEqualByComparingTo(BALANCE);
        assertThat(account.getCustomer().getId()).isEqualTo(1);
        assertThat(account.getCustomer().getName()).isEqualTo("tugrul aslan");
    }

    @Test
    public void shouldThrowExceptionWhenAccountDoesNotExist() {
        //given
        int id = 3;

        //when
        int statusCodeNotFound = HttpStatus.SC_NOT_FOUND;
        AccountNotFoundException exception = get(ACCOUNTS_URL + id)
                .then()
                .assertThat()
                .statusCode(statusCodeNotFound)
                .extract()
                .as(AccountNotFoundException.class);

        //then
        String expectedException = String.format("Given account with id '%s' not found", id);
        assertThat(exception).isNotNull();
        assertThat(exception.getStatusCode()).isEqualTo(statusCodeNotFound);
        assertThat(exception.getMessage()).isEqualTo(expectedException);
    }

    @Test
    public void shouldThrowExceptionWhenInvalidPathParameterProvided() {
        //given
        String pathParam = "THIS-IS-INVALID!";

        //when
        int statusCodeBadRequest = HttpStatus.SC_BAD_REQUEST;
        InvalidPathParameterException exception = get(ACCOUNTS_URL + pathParam)
                .then()
                .assertThat()
                .statusCode(statusCodeBadRequest)
                .extract()
                .as(InvalidPathParameterException.class);

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getStatusCode()).isEqualTo(statusCodeBadRequest);
        assertThat(exception.getMessage()).isEqualTo("Invalid path parameter provided");
    }

    @Test
    public void shouldTransferMoney() {
        //given
        TransferRequestDto transferRequest = createTransferRequest(TARGET_ACCOUNT_ID, new BigDecimal(500));

        //when
        AccountDto sourceAccount = given()
                .body(transferRequest)
                .post(ACCOUNTS_URL + SOURCE_ACCOUNT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_ACCEPTED)
                .extract()
                .as(AccountDto.class);

        //then
        AccountDto targetAccount = getAndAssertAccount(TARGET_ACCOUNT_ID);
        assertThat(sourceAccount).isNotNull();
        assertThat(targetAccount).isNotNull();
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo(new BigDecimal(2000));
        assertThat(targetAccount.getBalance()).isEqualByComparingTo(new BigDecimal(5000));
    }

    @Test
    public void shouldThrowExceptionWhenAccountHasInsufficientFunds() {
        //given
        BigDecimal amountToBeDeduced = new BigDecimal(55500);
        TransferRequestDto transferRequest = createTransferRequest(TARGET_ACCOUNT_ID, amountToBeDeduced);

        //when
        int statusCodeNotFound = HttpStatus.SC_BAD_REQUEST;
        InsufficientAccountBalanceException exception = given()
                .body(transferRequest)
                .post(ACCOUNTS_URL + SOURCE_ACCOUNT_ID)
                .then()
                .assertThat()
                .statusCode(statusCodeNotFound)
                .extract()
                .as(InsufficientAccountBalanceException.class);


        //then
        String expectedException = String.format("The Account '%s' has insufficient funds '%.02f' to deduce '%.02f'", SOURCE_ACCOUNT_ID, BALANCE, amountToBeDeduced);
        assertThat(exception).isNotNull();
        assertThat(exception.getStatusCode()).isEqualTo(statusCodeNotFound);
        assertThat(exception.getMessage()).isEqualTo(expectedException);
    }

    private AccountDto getAndAssertAccount(int id) {
        return get(ACCOUNTS_URL + id)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(AccountDto.class);
    }

    private TransferRequestDto createTransferRequest(int accountId, BigDecimal amount) {
        TransferRequestDto transferRequestDto = new TransferRequestDto();
        transferRequestDto.setTargetAccountId(accountId);
        transferRequestDto.setAmount(amount);
        return transferRequestDto;
    }
}