package mappers;

import java.math.RoundingMode;

import dto.ExchangeResponse;
import models.Currency;

public class ExchangeJsonMapper {
	public static String toJson(ExchangeResponse exchangeResponse) {
		StringBuilder result = new StringBuilder();
		
		Currency baseCurrency = exchangeResponse.getBaseCurrency();
		Currency targetCurrency = exchangeResponse.getTargetCurrency();
		
		String baseCurrencyJson = CurrencyJsonMapper.toJson(baseCurrency);
		String targetCurrencyJson = CurrencyJsonMapper.toJson(targetCurrency);
		
		result.append('{');
		result.append("\"baseCurrency\":");
		result.append(baseCurrencyJson);
		result.append(',');
		result.append("\"targetCurrency\":");
		result.append(targetCurrencyJson);
		result.append(',');
		result.append("\"rate\":" + exchangeResponse.getRate().setScale(2, RoundingMode.HALF_UP));
		result.append(',');
		result.append("\"amount\":" + exchangeResponse.getAmount().setScale(2, RoundingMode.HALF_UP));
		result.append(',');
		result.append("\"convertedAmount\":" + exchangeResponse.getConvertedAmount().setScale(2, RoundingMode.HALF_UP));
		result.append('}');
		
		return result.toString();
	}
}
