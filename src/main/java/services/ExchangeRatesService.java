package services;

import java.sql.SQLException;
import java.util.List;

import models.ExchangeRate;
import repository.ExchangeRatesRepository;

public class ExchangeRatesService {
	private final ExchangeRatesRepository exchangeRatesRepository;
	
	public ExchangeRatesService(ExchangeRatesRepository exchangeRatesRepository) {
		this.exchangeRatesRepository = exchangeRatesRepository;
	}
	
	public List<ExchangeRate> findAll() throws SQLException {
		return exchangeRatesRepository.findAll();
	}
}
