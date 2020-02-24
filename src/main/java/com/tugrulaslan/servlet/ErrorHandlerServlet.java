package com.tugrulaslan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugrulaslan.dto.ErrorMessageDto;
import com.tugrulaslan.exception.BusinessException;
import com.tugrulaslan.mapper.ErrorMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;

public class ErrorHandlerServlet extends HttpServlet {

    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";
    private final ErrorMapper errorMapper = ErrorMapper.INSTANCE;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        BusinessException exception = (BusinessException) servletRequest.getAttribute(ERROR_EXCEPTION);
        prepareResponse(servletResponse, exception);
    }

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        BusinessException exception = (BusinessException) servletRequest.getAttribute(ERROR_EXCEPTION);
        prepareResponse(servletResponse, exception);
    }

    private void prepareResponse(HttpServletResponse servletResponse, BusinessException exception) throws IOException {
        setResponseDetails(servletResponse, exception);
        writeErrorDetails(servletResponse, exception);
    }

    private void setResponseDetails(HttpServletResponse servletResponse, BusinessException exception) {
        servletResponse.setContentType(APPLICATION_JSON);
        servletResponse.setCharacterEncoding(UTF_8);
        servletResponse.setStatus(exception.getStatusCode());
    }

    private void writeErrorDetails(HttpServletResponse resp, BusinessException exception) throws IOException {
        try (PrintWriter writer = resp.getWriter()) {
            ErrorMessageDto errorMessageDto = errorMapper.toErrorMessageDto(exception.getStatusCode(), exception.getMessage());
            writer.write(objectMapper.writeValueAsString(errorMessageDto));
        }
    }
}