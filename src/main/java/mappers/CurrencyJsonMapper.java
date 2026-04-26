package mappers;

import java.util.List;

import models.Currency;

public class CurrencyJsonMapper {

	public static String toJson(Currency currency) {
		String result = "{";
		result += "\"id\":" + currency.getId() + ",";
		result += "\"name\":\"" + currency.getFullName() + "\",";
		result += "\"code\":\"" + currency.getCode() + "\",";
		result += "\"sign\":\"" + currency.getSign() + "\"";
		result += "}";
		return result;
	}

	public static String toJson(List<Currency> currencies) {
		StringBuilder result = new StringBuilder();
		result.append('[');
		for (Currency currencyDTO : currencies) {
			result.append(toJson(currencyDTO));
			result.append(',');
		}
		result.deleteCharAt(result.length() - 1);
		result.append(']');
		return result.toString();
	}

}
