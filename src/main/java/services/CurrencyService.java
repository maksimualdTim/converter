package services;

import java.sql.SQLException;
import java.util.List;

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
}