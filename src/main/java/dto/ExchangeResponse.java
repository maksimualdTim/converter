package dto;

import java.math.BigDecimal;

import models.Currency;

public class ExchangeResponse {
	private Currency baseCurrency;
	private Currency targetCurrency;
	private BigDecimal rate;
	private BigDecimal amount;
	private BigDecimal convertedAmount;
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getConvertedAmount() {
		return convertedAmount;
	}
	public void setConvertedAmount(BigDecimal convertedAmount) {
		this.convertedAmount = convertedAmount;
	}
	
	public Currency getBaseCurrency() {
		return baseCurrency;
	}
	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}
	public Currency getTargetCurrency() {
		return targetCurrency;
	}
	public void setTargetCurrency(Currency targetCurrency) {
		this.targetCurrency = targetCurrency;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
}