package mappers;


import java.util.List;

import models.Currency;
import models.ExchangeRate;

public class ExchaneRatesJsonMapper {
	public static String toJson(ExchangeRate exchangeRate) {
		StringBuilder result = new StringBuilder();
		
		Currency baseCurrency = exchangeRate.getBaseCurrency();
		Currency targetCurrency = exchangeRate.getTargetCurrency();
		
		String baseCurrencyJson = CurrencyJsonMapper.toJson(baseCurrency);
		String targetCurrencyJson = CurrencyJsonMapper.toJson(targetCurrency);
		
		result.append('{');
		result.append("\"id\":" + exchangeRate.getId() + ", \"baseCurrency\":");
		result.append(baseCurrencyJson);
		result.append(',');
		result.append("\"targetCurrency\":");
		result.append(targetCurrencyJson);
		result.append(',');
		result.append("\"rate\":" + exchangeRate.getRate());
		result.append('}');
		
		return result.toString();
	}
	
	public static String toJson(List<ExchangeRate> exchangeRates) {
		StringBuilder result = new StringBuilder();
		result.append('[');
		for (ExchangeRate exchangeRate : exchangeRates) {
			result.append(toJson(exchangeRate));
			result.append(',');
		}
		result.deleteCharAt(result.length() - 1);
		result.append(']');
		return result.toString();
	}
}
