package repository;

import models.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class CurrencyRepository {
    private final DataSource dataSource;

    public CurrencyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Currency> findAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT ID, Code, FullName, Sign FROM Currencies";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                currencies.add(mapRow(resultSet));
            }
        }

        return currencies;
    }

    public Currency findByCode(String code) throws SQLException {
        String sql = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE Code = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapRow(resultSet);
            }
        }
    }

    private Currency mapRow(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getInt("ID"));
        currency.setCode(resultSet.getString("Code"));
        currency.setFullName(resultSet.getString("FullName"));
        currency.setSign(resultSet.getString("Sign"));
        return currency;
    }
}