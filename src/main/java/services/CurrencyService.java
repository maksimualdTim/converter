package services;

import java.sql.SQLException;
import java.util.List;

import exceptions.CurrencyAlreadyExistsException;
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

    public Currency findByCode(String code) throws SQLException {
        if (code == null || code.isBlank()) {
            return null;
        }
        return currencyRepository.findByCode(code.toUpperCase());
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