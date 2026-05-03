package services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.Optional;

import dto.ExchangeResponse;
import exceptions.NotFoundException;
import models.ExchangeRate;
import repository.ExchangeRatesRepository;

public class ExchangeService {
	
	private final static String CROSS_RATE_CODE = "USD";
	
	private final ExchangeRatesRepository exchangeRatesRepository;
	
	public ExchangeService(ExchangeRatesRepository exchangeRatesRepository) {
		this.exchangeRatesRepository = exchangeRatesRepository;
	}
	
	public ExchangeResponse exchange(String baseCode, String targetCode, BigDecimal amount) throws NotFoundException, SQLException {
	    Optional<ExchangeRate> directRate = exchangeRatesRepository.findByCodes(baseCode, targetCode);
	    if (directRate.isPresent()) {
	    	return fillExchangeResponse(directRate.get(), amount);
	    }

	    Optional<ExchangeRate> reverseRate = exchangeRatesRepository.findByCodes(targetCode, baseCode);
	    if (reverseRate.isPresent()) {
	        BigDecimal rate = BigDecimal.ONE.divide(reverseRate.get().getRate(), MathContext.DECIMAL64);
	        reverseRate.get().setRate(rate);
	        
	        return fillExchangeResponse(reverseRate.get(), amount);
	    }

	    ExchangeRate usdToBase = exchangeRatesRepository.findByCodes(CROSS_RATE_CODE, baseCode)
	            .orElseThrow(() -> new NotFoundException("Exchange rate for " + baseCode + " not found"));
	    
	    ExchangeRate usdToTarget = exchangeRatesRepository.findByCodes(CROSS_RATE_CODE, targetCode)
	            .orElseThrow(() -> new NotFoundException("Exchange rate for " + targetCode + " not found"));

	    BigDecimal crossRate = usdToTarget.getRate().divide(usdToBase.getRate(), MathContext.DECIMAL64);
	    
	    ExchangeRate exchangeRate = new ExchangeRate();
	    exchangeRate.setBaseCurrency(usdToBase.getTargetCurrency());
	    exchangeRate.setTargetCurrency(usdToTarget.getTargetCurrency());
	    exchangeRate.setRate(crossRate);

	    return fillExchangeResponse(exchangeRate, amount);
	}
	
	private ExchangeResponse fillExchangeResponse(ExchangeRate exchangeRate, BigDecimal amount) {
    	ExchangeResponse exchangeResponse = new ExchangeResponse();
    	exchangeResponse.setAmount(amount);
    	exchangeResponse.setBaseCurrency(exchangeRate.getBaseCurrency());
    	exchangeResponse.setTargetCurrency(exchangeRate.getTargetCurrency());
    	exchangeResponse.setConvertedAmount(amount.multiply(exchangeRate.getRate()));
    	exchangeResponse.setRate(exchangeRate.getRate());
        return exchangeResponse;
	}
	
}
