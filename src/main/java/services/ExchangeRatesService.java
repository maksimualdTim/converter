package services;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import exceptions.AlreadyExistsException;
import exceptions.NotFoundException;
import models.ExchangeRate;
import repository.ExchangeRatesRepository;

public class ExchangeRatesService extends Service<ExchangeRate>{
	private final ExchangeRatesRepository exchangeRatesRepository;
	
	public ExchangeRatesService(ExchangeRatesRepository exchangeRatesRepository) {
		this.exchangeRatesRepository = exchangeRatesRepository;
	}
	
	public List<ExchangeRate> findAll() throws SQLException {
		return exchangeRatesRepository.findAll();
	}
	
	public ExchangeRate findByCodes(String codeBase, String codeTarget) throws SQLException, NotFoundException {
		Objects.requireNonNull(codeBase, "codeBase must not be null");
		Objects.requireNonNull(codeTarget, "codeTarget must not be null");
	    return exchangeRatesRepository.findByCodes(codeBase, codeTarget)
	            .orElseThrow(() -> new NotFoundException(
	                    "Exchange rate not found for " + codeBase + codeTarget
	            ));
	}
	
	public ExchangeRate create(ExchangeRate exchangeRate) throws SQLException, AlreadyExistsException {
        try {
        	return exchangeRatesRepository.save(exchangeRate);
        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new AlreadyExistsException("Exchange rate already exists");
            }
            throw e;
        }
	}
	
}
