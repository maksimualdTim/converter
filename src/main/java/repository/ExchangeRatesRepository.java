package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	
	public ExchangeRate findByCodes(String codeBase, String codeTarget) throws SQLException {		
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
					while (res.next()) {
						return mapExchangeRate(res);
					}	
				}
		}
		return null;
	}
	
	private ExchangeRate mapExchangeRate(ResultSet res) throws SQLException {
		ExchangeRate exchangeRate = new ExchangeRate();
		
		Currency targetCurrency = mapCurrency(res, "Base");
		Currency baseCurrency = mapCurrency(res, "Target");
		
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
