package repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import models.Currency;
import models.ExchangeRate;

public class ExchangeRatesRepository{
	private final DataSource dataSource;
	
	public ExchangeRatesRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public List<ExchangeRate> findAll() throws SQLException {
		List<ExchangeRate> result = new ArrayList<ExchangeRate>();
		
		String sql = """
				SELECT 
e.ID AS ExchangeRateId,
base.ID AS BaseCurrencyId,
base.Code AS BaseCurrencyCode,
base.Sign AS BaseCurrencySign,
base.FullName as BaseCurrencyFullName,
target.ID AS TargetCurrencyId,
target.Code AS TargetCurrencyCode,
target.Sign AS TargetCurrencySign,
target.FullName AS TargetCurrencyFullName,
e.Rate
FROM ExchangeRates e
JOIN Currencies base 
    ON base.ID = e.BaseCurrencyId
JOIN Currencies target 
    ON target.ID = e.TargetCurrencyId;
				""";
		
		try(Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement();
				ResultSet res = statement.executeQuery(sql)) {
				while (res.next()) {
					result.add(mapExchangeRate(res));
				}
		}
		
		return result;
	}
	
	public Optional<ExchangeRate> findByCodes(String codeBase, String codeTarget) throws SQLException {		
		String sql = """
SELECT 
	e.ID AS ExchangeRateId,
	base.ID AS BaseCurrencyId,
	base.Code AS BaseCurrencyCode,
	base.Sign AS BaseCurrencySign,
	base.FullName as BaseCurrencyFullName,
	target.ID AS TargetCurrencyId,
	target.Code AS TargetCurrencyCode,
	target.Sign AS TargetCurrencySign,
	target.FullName AS TargetCurrencyFullName,
	e.Rate
FROM ExchangeRates e
JOIN Currencies base 
    ON base.ID = e.BaseCurrencyId
JOIN Currencies target 
    ON target.ID = e.TargetCurrencyId
WHERE base.Code = ? AND target.Code = ?
				""";
		try(Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				) {
				statement.setString(1, codeBase);
				statement.setString(2, codeTarget);
				try (ResultSet res = statement.executeQuery()) {
					if (res.next()) {
						return Optional.of(mapExchangeRate(res));
					}	
				}
		}
		return Optional.empty();
	}
	
	public ExchangeRate save(ExchangeRate exchangeRate) throws SQLException {
		String sql = """
				INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
				VALUES (?, ?, ?)
				""";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				){
			statement.setInt(1, exchangeRate.getBaseCurrency().getId());
			statement.setInt(2, exchangeRate.getTargetCurrency().getId());
			statement.setBigDecimal(3, exchangeRate.getRate());
			
			int rowsAffected = statement.executeUpdate();
			
            if (rowsAffected != 1) {
                throw new SQLException("Expected 1 inserted row, but got: " + rowsAffected);
            }
            
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("ExchangeRate was inserted, but generated ID was not returned.");
                }

                exchangeRate.setId(keys.getInt(1));
                return exchangeRate;
            }
		}
	}
	
	public ExchangeRate update(ExchangeRate exchangeRate, BigDecimal rate) throws SQLException {
		String sql = "UPDATE ExchangeRates SET rate = ? WHERE ID= ?";
		
		try(Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setBigDecimal(1, rate);
			statement.setInt(2, exchangeRate.getId());
			
			statement.executeUpdate();
			exchangeRate.setRate(rate);
			return exchangeRate;
		}
	}
	
	private ExchangeRate mapExchangeRate(ResultSet res) throws SQLException {
		ExchangeRate exchangeRate = new ExchangeRate();
		
		Currency baseCurrency = mapCurrency(res, "Base");
		Currency targetCurrency = mapCurrency(res, "Target");
		
		exchangeRate.setId(res.getInt("ExchangeRateId"));
		exchangeRate.setRate(res.getBigDecimal("Rate"));
		exchangeRate.setBaseCurrency(baseCurrency);
		exchangeRate.setTargetCurrency(targetCurrency);
		
		return exchangeRate;
	}
	
	private Currency mapCurrency(ResultSet res, String basis) throws SQLException {
		Currency currency = new Currency();
		
		currency.setCode(res.getString(basis + "CurrencyCode"));
		currency.setSign(res.getString(basis + "CurrencySign"));
		currency.setFullName(res.getString(basis + "CurrencyFullName"));
		currency.setId(res.getInt(basis + "CurrencyId"));
		
		return currency;
	}
}
