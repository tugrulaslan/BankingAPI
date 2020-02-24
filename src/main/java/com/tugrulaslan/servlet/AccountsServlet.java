package com.tugrulaslan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugrulaslan.dao.AccountDao;
import com.tugrulaslan.dao.AccountDaoImpl;
import com.tugrulaslan.dto.AccountDto;
import com.tugrulaslan.dto.TransferRequestDto;
import com.tugrulaslan.exception.InvalidPathParameterException;
import com.tugrulaslan.mapper.AccountMapper;
import com.tugrulaslan.service.AccountService;
import com.tugrulaslan.service.AccountServiceImpl;
import com.tugrulaslan.service.AccountValidatorService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class AccountsServlet extends HttpServlet {
    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";
    private static final String PATH_REGEX = "/";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AccountMapper accountMapper = AccountMapper.INSTANCE;
    private final AccountDao accountDao;
    private final AccountValidatorService accountValidatorService;
    private final AccountService accountService;

    public AccountsServlet() {
        this.accountDao = new AccountDaoImpl();
        this.accountValidatorService = new AccountValidatorService(accountDao);
        this.accountService = new AccountServiceImpl(accountDao, accountMapper,
                accountValidatorService);
    }

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws IOException {
        setResponseDetails(servletResponse);
        int accountId = retrieveAccountIdPathParameter(servletRequest);
        AccountDto account = accountService.retrieveById(accountId);
        try (PrintWriter writer = servletResponse.getWriter()) {
            writer.write(objectMapper.writeValueAsString(account));
        }
    }

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws IOException {
        setResponseDetails(servletResponse);
        servletResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
        int accountId = retrieveAccountIdPathParameter(servletRequest);
        TransferRequestDto transferRequestDto = objectMapper.readValue(servletRequest.getReader(), TransferRequestDto.class);
        AccountDto transfer = accountService.transfer(accountId, transferRequestDto);
        try (PrintWriter writer = servletResponse.getWriter()) {
            writer.write(objectMapper.writeValueAsString(transfer));
        }
    }

    private Integer retrieveAccountIdPathParameter(HttpServletRequest servletRequest) {
        String pathInfo = servletRequest.getPathInfo();
        String[] pathParts = pathInfo.split(PATH_REGEX);
        return Arrays.stream(pathParts)
                .filter(StringUtils::isNotEmpty)
                .filter(StringUtils::isNumeric)
                .findFirst()
                .map(Integer::valueOf)
                .orElseThrow(() -> new InvalidPathParameterException("Invalid path parameter provided"));
    }

    private void setResponseDetails(HttpServletResponse servletResponse) {
        servletResponse.setContentType(APPLICATION_JSON);
        servletResponse.setCharacterEncoding(UTF_8);
    }
}