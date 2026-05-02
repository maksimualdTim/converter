package services;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import exceptions.CurrencyAlreadyExistsException;
import exceptions.NotFoundException;
import models.Currency;
import repository.CurrencyRepository;

public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository repo) {
        this.currencyRepository = repo;
    }

    public List<Currency> findAll() throws SQLException {
        return currencyRepository.findAll();
    }

    public Currency findByCode(String code) throws SQLException, NotFoundException {
    	Objects.requireNonNull(code);
        return currencyRepository.findByCode(code.toUpperCase())
        		.orElseThrow(() -> new NotFoundException("Currency not found"));
    }
    
    public Currency create(Currency currency) throws SQLException{
        try {

            return currencyRepository.save(currency);

        } catch (SQLException e) {

            if (isUniqueConstraintViolation(e)) {
                throw new CurrencyAlreadyExistsException("Currency code or sign already exists");
            }
            throw e;
        }
	}
    
    private boolean isUniqueConstraintViolation(SQLException e) {

        return "23000".equals(e.getSQLState()) && e.getErrorCode() == 1062;

    }
}