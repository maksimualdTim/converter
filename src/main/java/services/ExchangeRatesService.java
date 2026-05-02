package services;

import java.sql.SQLException;
import java.util.List;

import exceptions.ExchangeRateNotFoundException;
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
	
	public ExchangeRate findByCodes(String codeBase, String codeTarget) throws SQLException, ExchangeRateNotFoundException {
	    return exchangeRatesRepository.findByCodes(codeBase, codeTarget)
	            .orElseThrow(() -> new ExchangeRateNotFoundException(
	                    "Exchange rate not found for " + codeBase + codeTarget
	            ));
	}
}
