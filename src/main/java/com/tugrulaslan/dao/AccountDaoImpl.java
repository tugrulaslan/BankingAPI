package com.tugrulaslan.dao;

import com.tugrulaslan.entity.AccountEntity;
import com.tugrulaslan.entity.CustomerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

public class AccountDaoImpl implements AccountDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDaoImpl.class);

    private static final String URL = "jdbc:h2:mem:tcp://127.0.1.1:9094/mydb2";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static final String CUSTOMER_WHERE_QUERY = "select * from customers c where c.id = ?";
    private static final String ACCOUNT_WHERE_QUERY = "select * from accounts a where a.id = ?";
    private static final String DEDUCE_ACCOUNT_UPDATE_QUERY = "update accounts set balance = balance - ? where id = ?";
    private static final String DEPOSIT_ACCOUNT_UPDATE_QUERY = "update accounts set balance = balance + ? where id = ?";

    @Override
    public Optional<AccountEntity> findAccountById(int id) {
        CustomerEntity customerEntity = new CustomerEntity();
        AccountEntity accountEntity = new AccountEntity();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(ACCOUNT_WHERE_QUERY);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                retrieveCustomerData(connection, customerEntity, customerId);
                mapAccountData(resultSet, customerEntity, accountEntity);
                return Optional.of(accountEntity);
            }
        } catch (SQLException e) {
            LOGGER.error("Error ", e);
        }
        return Optional.empty();
    }

    private void retrieveCustomerData(Connection connection, CustomerEntity customerEntity, int customerId) throws SQLException {
        PreparedStatement customerPreparedStatement = connection.prepareStatement(CUSTOMER_WHERE_QUERY);
        customerPreparedStatement.setInt(1, customerId);
        ResultSet customerResultSet = customerPreparedStatement.executeQuery();
        while (customerResultSet.next()) {
            mapCustomerData(customerEntity, customerResultSet);
        }
    }

    private void mapCustomerData(CustomerEntity customerEntity, ResultSet customerResultSet) throws SQLException {
        customerEntity.setId(customerResultSet.getInt("id"));
        customerEntity.setName(customerResultSet.getString("name"));
    }

    private void mapAccountData(ResultSet rs, CustomerEntity customerEntity, AccountEntity accountEntity) throws SQLException {
        accountEntity.setId(rs.getInt("id"));
        accountEntity.setBalance(rs.getBigDecimal("balance"));
        accountEntity.setCustomer(customerEntity);
    }

    @Override
    public void updateAmount(int sourceAccountId, int targetAccountId, BigDecimal amount) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            deduceSourceAccount(sourceAccountId, amount, connection);
            depositTargetAccount(targetAccountId, amount, connection);
        } catch (SQLException e) {
            LOGGER.error("Error ", e);
        }
    }

    private void deduceSourceAccount(int sourceAccountId, BigDecimal amount, Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(DEDUCE_ACCOUNT_UPDATE_QUERY);
        preparedStatement.setBigDecimal(1, amount);
        preparedStatement.setInt(2, sourceAccountId);
        preparedStatement.executeUpdate();
    }

    private void depositTargetAccount(int targetAccountId, BigDecimal amount, Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(DEPOSIT_ACCOUNT_UPDATE_QUERY);
        preparedStatement.setBigDecimal(1, amount);
        preparedStatement.setInt(2, targetAccountId);
        preparedStatement.executeUpdate();
    }
}
