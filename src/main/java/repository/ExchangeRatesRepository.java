package repository;

import java.sql.Connection;
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
					Currency targetCurrency = new Currency();
					Currency baseCurrency = new Currency();
					ExchangeRate exchangeRate = new ExchangeRate();
					
					baseCurrency.setCode(res.getString("BaseCurrencyCode"));
					baseCurrency.setSign(res.getString("BaseCurrencySign"));
					baseCurrency.setFullName(res.getString("BaseCurrencyFullName"));
					baseCurrency.setId(res.getInt("BaseCurrencyId"));
					
					targetCurrency.setCode(res.getString("TargetCurrencyCode"));
					targetCurrency.setSign(res.getString("TargetCurrencySign"));
					targetCurrency.setFullName(res.getString("TargetCurrencyFullName"));
					targetCurrency.setId(res.getInt("TargetCurrencyId"));
					
					exchangeRate.setId(res.getInt("ExchangeRateId"));
					exchangeRate.setRate(res.getBigDecimal("Rate"));
					exchangeRate.setBaseCurrency(baseCurrency);
					exchangeRate.setTargetCurrency(targetCurrency);
					result.add(exchangeRate);
				}
		}
		
		return result;
	}
}
